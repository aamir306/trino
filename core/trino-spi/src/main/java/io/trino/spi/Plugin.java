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
package io.trino.spi;

import io.trino.spi.block.BlockEncoding;
import io.trino.spi.catalog.CatalogStoreFactory;
import io.trino.spi.connector.ConnectorFactory;
import io.trino.spi.eventlistener.EventListenerFactory;
import io.trino.spi.exchange.ExchangeManagerFactory;
import io.trino.spi.filesystem.TrinoFileSystemFactory;
import io.trino.spi.function.LanguageFunctionEngine;
import io.trino.spi.resourcegroups.ResourceGroupConfigurationManagerFactory;
import io.trino.spi.security.CertificateAuthenticatorFactory;
import io.trino.spi.security.GroupProviderFactory;
import io.trino.spi.security.HeaderAuthenticatorFactory;
import io.trino.spi.security.PasswordAuthenticatorFactory;
import io.trino.spi.security.SystemAccessControlFactory;
import io.trino.spi.session.SessionPropertyConfigurationManagerFactory;
import io.trino.spi.spool.SpoolingManagerFactory;
import io.trino.spi.type.ParametricType;
import io.trino.spi.type.Type;

import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public interface Plugin
{
    default Iterable<CatalogStoreFactory> getCatalogStoreFactories()
    {
        return emptyList();
    }

    default Iterable<ConnectorFactory> getConnectorFactories()
    {
        return emptyList();
    }

    default Iterable<BlockEncoding> getBlockEncodings()
    {
        return emptyList();
    }

    default Iterable<Type> getTypes()
    {
        return emptyList();
    }

    default Iterable<ParametricType> getParametricTypes()
    {
        return emptyList();
    }

    default Set<Class<?>> getFunctions()
    {
        return emptySet();
    }

    default Iterable<LanguageFunctionEngine> getLanguageFunctionEngines()
    {
        return emptyList();
    }

    default Iterable<SystemAccessControlFactory> getSystemAccessControlFactories()
    {
        return emptyList();
    }

    default Iterable<GroupProviderFactory> getGroupProviderFactories()
    {
        return emptyList();
    }

    default Iterable<PasswordAuthenticatorFactory> getPasswordAuthenticatorFactories()
    {
        return emptyList();
    }

    default Iterable<HeaderAuthenticatorFactory> getHeaderAuthenticatorFactories()
    {
        return emptyList();
    }

    default Iterable<CertificateAuthenticatorFactory> getCertificateAuthenticatorFactories()
    {
        return emptyList();
    }

    default Iterable<EventListenerFactory> getEventListenerFactories()
    {
        return emptyList();
    }

    default Iterable<ResourceGroupConfigurationManagerFactory> getResourceGroupConfigurationManagerFactories()
    {
        return emptyList();
    }

    default Iterable<SessionPropertyConfigurationManagerFactory> getSessionPropertyConfigurationManagerFactories()
    {
        return emptyList();
    }

    default Iterable<ExchangeManagerFactory> getExchangeManagerFactories()
    {
        return emptyList();
    }

    default Iterable<SpoolingManagerFactory> getSpoolingManagerFactories()
    {
        return emptyList();
    }

    /**
     * File system factories supplied by this plugin. The engine registers each returned
     * factory under its {@link TrinoFileSystemFactory#getName() name} (URI scheme) and
     * routes matching paths through it.
     *
     * <p>Use cases:
     * <ul>
     *   <li>External blob caches that interpose on file reads (e.g. shelf, an OSS
     *       Iceberg-native cache; Alluxio in a forthcoming refactor).
     *   <li>Custom storage backends not covered by the built-in S3/GCS/Azure factories.
     * </ul>
     *
     * <p>Plugin-supplied factories are layered on top of the built-in factories; a
     * plugin returning a factory with name {@code "s3"} will collide with the built-in
     * S3 binding at registration time.
     *
     * @see io.trino.spi.filesystem.TrinoFileSystemFactory
     */
    default Iterable<TrinoFileSystemFactory> getFileSystemFactories()
    {
        return emptyList();
    }
}
