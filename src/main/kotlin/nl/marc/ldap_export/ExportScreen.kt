package nl.marc.ldap_export

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.directory.api.ldap.model.cursor.EntryCursor
import org.apache.directory.api.ldap.model.message.SearchScope
import org.apache.directory.ldap.client.api.LdapConnection
import java.nio.file.Path
import kotlin.io.path.div

@Composable
fun ExportScreen(ldapConnection: LdapConnection) {
    val coroutineScope = rememberCoroutineScope()
    var firstName by remember { mutableStateOf("") }
    var organisationUnit by remember { mutableStateOf("Medical School - Faculty and Staff") }
    var exportedFilePath by remember { mutableStateOf<String?>(null) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            label = { Text("First name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = organisationUnit,
            onValueChange = {
                organisationUnit = it
            },
            label = { Text("Organisation unit") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    ldapConnection.search(
                        "ou=People,dc=umich,dc=edu",
                        "(& (ou=$organisationUnit) (cn=${firstName}*) (mail=*@*))",
                        SearchScope.SUBTREE
                    ).use { entries ->
                        val resourceDir = ResourceLoader().getResourcesDir()
                        val filePath = resourceDir / "$organisationUnit - ${firstName}.txt"
                        println("Writing to $filePath")
                        exportedFilePath = filePath.toString()
                        writeEntriesToFile(entries, filePath)
                    }
                }
            }
        ) {
            Text("Export")
        }

        if (exportedFilePath != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Writing/written exported file to: $exportedFilePath")
        }
    }
}

fun writeEntriesToFile(entries: EntryCursor, filePath: Path) {
    val file = filePath.toFile()
    file.outputStream().use {
        it.bufferedWriter().use {
            for (entry in entries) {
                it.write("${entry.dn}\r\n")
                it.write("  name: ${entry["cn"].joinToString { it.string }}\r\n")
                it.write("  mail: ${entry["mail"].joinToString { it.string }}\r\n")
                it.write("\r\n")
            }
        }
    }
}
