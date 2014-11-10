/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.lang;


/**
 * {@code Exception} is the superclass of all classes that represent recoverable
 * exceptions. When exceptions are thrown, they may be caught by application
 * code.
 *
 * @see Throwable
 * @see Error
 * @see RuntimeException
 */
public class Exception extends Throwable {
    private static final long serialVersionUID = -3387516993124229948L;

    private transient Class[] classContext = SecurityManagerEx.getInstance().getThrowableContext(this);

    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public Exception() {
        super();
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public Exception(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage
     *            the detail message for this exception.
     * @param throwable
     *            the cause of this exception.
     */
    public Exception(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified cause.
     *
     * @param throwable
     *            the cause of this exception.
     */
    public Exception(Throwable throwable) {
        super(throwable);
    }

    public Class[] getClassContext() {
        return classContext;
    }

    private static class SecurityManagerEx extends SecurityManager
    {

        private static SecurityManagerEx sm;

        public static SecurityManagerEx getInstance() {
            // No synchronized block because we don't really care
            // if multiple instances are created at some point
            if (sm == null) {
                sm = new SecurityManagerEx();
            }
            return sm;
        }

        public Class[] getThrowableContext(Throwable t) {
            try {
                Class[] context = getClassContext();
                int nb = 0;
                for (;;) {
                    if (context[context.length - 1 - nb] == t.getClass()) {
                        break;
                    }
                    nb++;
                }
                Class[] nc = new Class[nb];
                System.arraycopy(context, context.length - nb, nc, 0, nb);
                return nc;
            } catch (Exception e) {
                return null;
            }
        }
    }

}
