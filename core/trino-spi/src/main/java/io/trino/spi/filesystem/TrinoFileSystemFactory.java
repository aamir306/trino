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
package io.trino.spi.filesystem;

import io.trino.spi.connector.ConnectorSession;
import io.trino.spi.security.ConnectorIdentity;

/**
 * SPI marker for plugin-supplied file system factories.
 *
 * <p>Plugins return instances from {@link io.trino.spi.Plugin#getFileSystemFactories()}.
 * Implementations live in {@code lib/trino-filesystem} and elsewhere; the engine downcasts
 * the returned object to the engine-internal {@code io.trino.filesystem.TrinoFileSystem}
 * (or richer subtypes) at registration time. {@code Object} is used in the SPI signature
 * deliberately to keep the engine-internal {@code TrinoFileSystem} surface (which carries
 * many richer types not yet hoisted into the SPI) out of the plugin/SPI dependency
 * boundary.
 *
 * <p>Plugins SHOULD return concrete implementations of
 * {@code io.trino.filesystem.TrinoFileSystemFactory}, which extends this interface and
 * narrows the {@link #create} return type to the engine {@code TrinoFileSystem} via
 * Java covariant returns.
 *
 * <p>{@link #getName()} returns a stable URI scheme handle (for example {@code "s3"},
 * {@code "shelf"}, {@code "alluxio"}). The engine routes paths matching {@code &lt;name&gt;://...}
 * through the registered factory.
 *
 * @see io.trino.spi.Plugin#getFileSystemFactories()
 */
public interface TrinoFileSystemFactory
{
    /**
     * URI scheme handled by this factory (for example {@code "s3"}, {@code "shelf"}).
     * Must be stable across restarts. Two plugin-supplied factories returning the same
     * name will collide at registration time.
     */
    String getName();

    /**
     * Create a per-identity file system handle.
     *
     * <p>Returns {@code Object} at the SPI boundary; the engine downcasts to
     * {@code io.trino.filesystem.TrinoFileSystem}. Implementations in
     * {@code lib/trino-filesystem} narrow this return type via covariant overrides.
     */
    Object create(ConnectorIdentity identity);

    /**
     * Convenience overload using a {@link ConnectorSession}'s identity.
     */
    default Object create(ConnectorSession session)
    {
        return create(session.getIdentity());
    }
}
