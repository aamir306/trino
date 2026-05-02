/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.filesystem;

import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.security.ConnectorIdentity;

public interface TrinoFileSystemFactory
        extends io.trino.spi.filesystem.TrinoFileSystemFactory
{
    @Override
    TrinoFileSystem create(ConnectorIdentity identity);

    @Override
    default TrinoFileSystem create(ConnectorSession session)
    {
        return create(session.getIdentity());
    }

    /**
     * Default name uses the implementation class simple name. Engine-internal factories
     * (S3, GCS, Azure, ...) are still registered by URI scheme via Guice binding in
     * {@code FileSystemModule} and do not rely on this default. Plugin-supplied
     * factories returned from {@link io.trino.spi.Plugin#getFileSystemFactories()}
     * SHOULD override this with their stable URI scheme.
     */
    @Override
    default String getName()
    {
        return getClass().getSimpleName();
    }
}
