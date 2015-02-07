/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.ignitefs;

import org.jetbrains.annotations.*;

/**
 * Exception thrown when target path supposed to be created already exists.
 */
public class IgniteFsPathAlreadyExistsException extends IgniteFsInvalidPathException {
    /** */
    private static final long serialVersionUID = 0L;

    /**
     * @param msg Error message.
     */
    public IgniteFsPathAlreadyExistsException(String msg) {
        super(msg);
    }

    /**
     * @param cause Exception cause.
     */
    public IgniteFsPathAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    /**
     * @param msg Error message.
     * @param cause Exception cause.
     */
    public IgniteFsPathAlreadyExistsException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}