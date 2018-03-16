   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at


   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
        
package org.apache.poi.hpsf;

/**
 * <p>This exception is thrown when trying to write a (yet) unsupported variant
 * type.</p>
 * 
 * @see ReadingNotSupportedException
 * @see UnsupportedVariantTypeException
 *
 * @author Rainer Klute <a
 * href="mailto:klute@rainer-klute.de">&lt;klute@rainer-klute.de&gt;</a>
 * @since 2003-08-08
 * @version $Id: WritingNotSupportedException.java 489730 2006-12-22 19:18:16Z bayard $
 */
public class WritingNotSupportedException
    extends UnsupportedVariantTypeException
{

    /**
     * <p>Constructor</p>
     * 
     * @param variantType The unsupported variant type.
     * @param value The value.
     */
    public WritingNotSupportedException(final long variantType,
                                        final Object value)
    {
        super(variantType, value);
    }

}