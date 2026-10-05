package com.example.mypage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

class SecActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val extras = intent.extras
        val receivedText = extras?.getString("page_one_text") ?: intent.getStringExtra("page_one_text") ?: ""

        setContent {
            MyPageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageTwoScreen(
                        modifier = Modifier.padding(innerPadding),
                        receivedText = receivedText,
                        onSendBackToPageOne = { textToSendBack ->
                            val resultIntent = Intent()
                            val bundle = Bundle().apply {
                                putString("page_two_text", textToSendBack)
                            }
                            resultIntent.putExtras(bundle)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun PageTwoScreen(
    modifier: Modifier = Modifier,
    receivedText: String,
    onSendBackToPageOne: (String) -> Unit,
) {
    var inputText by remember { mutableStateOf(receivedText) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "第二頁",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "第一頁傳來的文字：",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = receivedText,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("文字輸入框") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSendBackToPageOne(inputText) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("傳回第一頁")
        }
    }
}
