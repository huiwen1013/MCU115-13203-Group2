package com.example.mypage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mypage.ui.theme.MyPageTheme

class MainActivity : ComponentActivity() {

    private var returnedText by mutableStateOf<String?>(null)

    private val startSecActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val text = data?.extras?.getString("page_two_text") ?: data?.getStringExtra("page_two_text")
                text?.let { returnedText = it }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageOneScreen(
                        modifier = Modifier.padding(innerPadding),
                        returnedText = returnedText,
                        onSendToPageTwo = { textToSend ->
                            val intent = Intent(this, SecActivity::class.java)
                            val bundle = Bundle().apply {
                                putString("page_one_text", textToSend)
                            }
                            intent.putExtras(bundle)
                            startSecActivityLauncher.launch(intent)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun PageOneScreen(
    modifier: Modifier = Modifier,
    returnedText: String?,
    onSendToPageTwo: (String) -> Unit,
) {
    var inputText by remember { mutableStateOf("This is the text from the page one") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "第一頁",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("文字輸入框") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSendToPageTwo(inputText) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("切換頁面（傳送到第二頁）")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!returnedText.isNullOrEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "第二頁回傳的文字：",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = returnedText,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}
