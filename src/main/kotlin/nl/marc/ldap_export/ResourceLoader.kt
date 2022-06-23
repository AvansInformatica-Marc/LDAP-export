package nl.marc.ldap_export

import kotlin.io.path.toPath

class ResourceLoader {
    fun getResourcesDir() = this.javaClass.getResource("/").toURI().toPath()
}
