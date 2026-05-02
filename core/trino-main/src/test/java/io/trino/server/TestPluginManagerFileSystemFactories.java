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
package io.trino.server;

import io.trino.filesystem.TrinoFileSystem;
import io.trino.filesystem.manager.FileSystemFactoryRegistry;
import io.trino.spi.Plugin;
import io.trino.spi.security.ConnectorIdentity;
import org.junit.jupiter.api.Test;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class TestPluginManagerFileSystemFactories
{
    @Test
    public void testEmptyByDefault()
    {
        FileSystemFactoryRegistry registry = new FileSystemFactoryRegistry();
        assertThat(registry.getFactories()).isEmpty();
    }

    @Test
    public void testRegistersPluginSuppliedFactory()
    {
        FileSystemFactoryRegistry registry = new FileSystemFactoryRegistry();
        TestingFileSystemFactory factory = new TestingFileSystemFactory("shelf");
        registry.addFactory(factory);

        assertThat(registry.getFactories()).containsKey("shelf");
        assertThat(registry.getFactories().get("shelf")).isSameAs(factory);
    }

    @Test
    public void testDuplicateNameRejected()
    {
        FileSystemFactoryRegistry registry = new FileSystemFactoryRegistry();
        registry.addFactory(new TestingFileSystemFactory("shelf"));

        assertThatThrownBy(() -> registry.addFactory(new TestingFileSystemFactory("shelf")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("'shelf' is already registered");
    }

    @Test
    public void testPluginGetFileSystemFactoriesDefaultsEmpty()
    {
        Plugin plugin = new Plugin() {};
        assertThat(plugin.getFileSystemFactories()).isEmpty();
    }

    @Test
    public void testPluginCanReturnFileSystemFactories()
    {
        TestingFileSystemFactory custom = new TestingFileSystemFactory("custom");
        Plugin plugin = new Plugin()
        {
            @Override
            public Iterable<io.trino.spi.filesystem.TrinoFileSystemFactory> getFileSystemFactories()
            {
                return of(custom);
            }
        };

        assertThat(plugin.getFileSystemFactories()).containsExactly(custom);
    }

    /** Test factory implementing the engine-internal subtype (matches the production pattern). */
    private static final class TestingFileSystemFactory
            implements io.trino.filesystem.TrinoFileSystemFactory
    {
        private final String name;

        TestingFileSystemFactory(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public TrinoFileSystem create(ConnectorIdentity identity)
        {
            return mock(TrinoFileSystem.class);
        }
    }
}
