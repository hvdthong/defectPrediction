   Copyright 2002-2004   Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at


   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        

package org.apache.poi.hssf.record;

/**
 * Title:     Record Format Exception
 * Description: Used by records to indicate invalid format/data.<P>
 */

public class RecordFormatException
    extends RuntimeException
{
    public RecordFormatException(String exception)
    {
        super(exception);
    }
}
