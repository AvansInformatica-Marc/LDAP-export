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
import org.apache.directory.ldap.client.api.LdapConnection
import org.apache.directory.ldap.client.api.LdapNetworkConnection

@Composable
fun LoadLdapConnection(onLdapConnectionAvailable: (LdapConnection) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var host by remember { mutableStateOf("ldap.itd.umich.edu") }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = host,
            onValueChange = {
                host = it
            },
            label = { Text("LDAP directory") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val connection = LdapNetworkConnection(host, 389)
                    connection.setTimeOut(0L)
                    connection.bind()
                    onLdapConnectionAvailable(connection)
                }
            }
        ) {
            Text("Connect")
        }
    }
}
