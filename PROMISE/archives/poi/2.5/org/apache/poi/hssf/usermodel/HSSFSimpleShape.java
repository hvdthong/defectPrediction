   Copyright 2004   Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at


   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hssf.usermodel;

/**
 * Represents a simple shape such as a line, rectangle or oval.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class HSSFSimpleShape
    extends HSSFShape
{

    public final static short       OBJECT_TYPE_LINE               = 1;
    public final static short       OBJECT_TYPE_RECTANGLE          = 2;
    public final static short       OBJECT_TYPE_OVAL               = 3;

    int shapeType = OBJECT_TYPE_LINE;

    HSSFSimpleShape( HSSFShape parent, HSSFAnchor anchor )
    {
        super( parent, anchor );
    }

    /**
     * Gets the shape type.
     * @return  One of the OBJECT_TYPE_* constants.
     *
     * @see #OBJECT_TYPE_LINE
     * @see #OBJECT_TYPE_OVAL
     * @see #OBJECT_TYPE_RECTANGLE
     */
    public int getShapeType() { return shapeType; }

    /**
     * Sets the shape types.
     *
     * @param shapeType One of the OBJECT_TYPE_* constants.
     *
     * @see #OBJECT_TYPE_LINE
     * @see #OBJECT_TYPE_OVAL
     * @see #OBJECT_TYPE_RECTANGLE
     */
    public void setShapeType( int shapeType ){ this.shapeType = shapeType; }

}
