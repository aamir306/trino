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
package io.trino.filesystem.manager;

import com.google.errorprone.annotations.ThreadSafe;
import io.airlift.log.Logger;
import io.trino.filesystem.TrinoFileSystemFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Engine-wide registry of plugin-supplied {@link TrinoFileSystemFactory} instances.
 *
 * <p>Populated by {@code PluginManager} as plugins are loaded, consumed by
 * {@code FileSystemModule} during catalog instantiation. Built-in factories
 * (S3/GCS/Azure/Local/Alluxio) are registered via Guice in {@code FileSystemModule}
 * and DO NOT pass through this registry.
 *
 * <p>Names (URI schemes) are unique across the engine; a plugin attempting to
 * register a factory under an already-bound name will fail fast.
 */
@ThreadSafe
public class FileSystemFactoryRegistry
{
    private static final Logger log = Logger.get(FileSystemFactoryRegistry.class);

    private final Map<String, TrinoFileSystemFactory> factories = new ConcurrentHashMap<>();

    public void addFactory(TrinoFileSystemFactory factory)
    {
        requireNonNull(factory, "factory is null");
        String name = requireNonNull(factory.getName(), "factory.getName() is null");
        TrinoFileSystemFactory existing = factories.putIfAbsent(name, factory);
        if (existing != null) {
            throw new IllegalStateException(
                    "File system factory '" + name + "' is already registered: " + existing.getClass().getName());
        }
        log.info("Registered plugin-supplied file system factory %s for scheme '%s'", factory.getClass().getName(), name);
    }

    public Map<String, TrinoFileSystemFactory> getFactories()
    {
        return Map.copyOf(factories);
    }
}
