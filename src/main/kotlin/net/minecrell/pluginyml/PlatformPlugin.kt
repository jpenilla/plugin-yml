/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Minecrell <https://github.com/Minecrell>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.minecrell.pluginyml

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

abstract class PlatformPlugin<T : PluginDescription>(private val platformName: String, private val fileName: String) : Plugin<Project> {

    protected abstract fun createExtension(project: Project): T

    final override fun apply(project: Project) {
        project.run {
            val description = createExtension(this)

            // Add extension
            extensions.add(platformName.decapitalize(), description)

            // Create task
            val generateTask = tasks.register<GeneratePluginDescription>("generate${platformName}PluginDescription") {
                fileName.set(this@PlatformPlugin.fileName)
                pluginDescription.set(provider {
                    setDefaults(project, description)
                    description
                })

                doFirst {
                    validate(description)
                }
            }

            plugins.withType<JavaPlugin> {
                tasks.named<AbstractCopyTask>("processResources") {
                    from(generateTask)
                }
            }
        }
    }

    protected abstract fun setDefaults(project: Project, description: T)
    protected abstract fun validate(description: T)

}
