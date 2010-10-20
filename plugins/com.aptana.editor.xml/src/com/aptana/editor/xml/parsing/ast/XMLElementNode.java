/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class XMLElementNode extends XMLNode
{
	/**
	 * getTagName
	 * 
	 * @param tag
	 * @return
	 */
	private static String getTagName(String tag)
	{
		// the first element is the tag name
		StringTokenizer token = new StringTokenizer(tag);
		return token.nextToken();
	}

	private INameNode fNameNode;
	private boolean fIsSelfClosing;
	private Map<String, String> fAttributes;

	/**
	 * XMLElementNode
	 * 
	 * @param tag
	 * @param start
	 * @param end
	 */
	public XMLElementNode(String tag, int start, int end)
	{
		this(tag, new XMLNode[0], start, end);
	}

	/**
	 * XMLElementNode
	 * 
	 * @param tag
	 * @param children
	 * @param start
	 * @param end
	 */
	public XMLElementNode(String tag, XMLNode[] children, int start, int end)
	{
		super(XMLNodeType.ELEMENT, children, start, end);

		if (tag.length() > 0)
		{
			try
			{
				if (tag.endsWith("/>")) //$NON-NLS-1$
				{
					// self-closing
					tag = getTagName(tag.substring(1, tag.length() - 2));
					fIsSelfClosing = true;
				}
				else
				{
					tag = getTagName(tag.substring(1, tag.length() - 1));
				}
			}
			catch (IndexOutOfBoundsException e)
			{
			}
		}

		fNameNode = new NameNode(tag, start, end);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#addOffset(int)
	 */
	@Override
	public void addOffset(int offset)
	{
		IRange range = fNameNode.getNameRange();

		fNameNode = new NameNode(fNameNode.getName(), range.getStartingOffset() + offset, range.getEndingOffset() + offset);

		super.addOffset(offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}

		if (!(obj instanceof XMLElementNode))
		{
			return false;
		}

		return getName().equals(((XMLElementNode) obj).getName());
	}

	/**
	 * getAttribute
	 * 
	 * @param name
	 * @return
	 */
	public String getAttibute(String name)
	{
		String result = ""; //$NON-NLS-1$

		if (fAttributes != null)
		{
			result = fAttributes.get(name);
		}

		return result;
	}

	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return fNameNode.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNameNode()
	 */
	@Override
	public INameNode getNameNode()
	{
		return fNameNode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getText()
	 */
	@Override
	public String getText()
	{
		return fNameNode.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + getName().hashCode();
	}

	/**
	 * isSelfClosing
	 * 
	 * @return
	 */
	public boolean isSelfClosing()
	{
		return fIsSelfClosing;
	}

	/**
	 * setAttribute
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value)
	{
		if (fAttributes == null)
		{
			fAttributes = new HashMap<String, String>();
		}

		fAttributes.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		String name = getName();

		if (name.length() > 0)
		{
			text.append("<").append(name); //$NON-NLS-1$
			text.append(">"); //$NON-NLS-1$

			IParseNode[] children = getChildren();

			for (IParseNode child : children)
			{
				text.append(child);
			}

			text.append("</").append(name).append(">"); //$NON-NLS-1$//$NON-NLS-2$
		}

		return text.toString();
	}
}
