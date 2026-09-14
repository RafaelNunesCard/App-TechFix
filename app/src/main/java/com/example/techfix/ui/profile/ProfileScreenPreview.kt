package com.example.techfix.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.techfix.ui.theme.TechFixTheme

@Preview(showBackground = true, backgroundColor = 0xFF121212, widthDp = 380, heightDp = 800)
@Composable
private fun ProfileScreenPreview() {
    TechFixTheme {
        ProfileScreen()
    }
}
