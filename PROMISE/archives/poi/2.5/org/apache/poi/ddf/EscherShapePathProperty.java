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
package org.apache.poi.ddf;

/**
 * Defines the constants for the various possible shape paths.
 *
 * @author Glen Stampoultzis (glens at apache.org)
 */
public class EscherShapePathProperty
        extends EscherSimpleProperty
{

    public static final int LINE_OF_STRAIGHT_SEGMENTS = 0;
    public static final int CLOSED_POLYGON = 1;
    public static final int CURVES = 2;
    public static final int CLOSED_CURVES = 3;
    public static final int COMPLEX = 4;

    public EscherShapePathProperty( short propertyNumber, int shapePath )
    {
        super( propertyNumber, false, false, shapePath );
    }



}
