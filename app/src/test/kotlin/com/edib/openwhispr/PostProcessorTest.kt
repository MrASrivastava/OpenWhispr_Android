package com.edib.openwhispr

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PostProcessorTest {

    @Test
    fun parseSuccess() {
        val json = """
        {
            "id": "chatcmpl-123",
            "object": "chat.completion",
            "created": 1677652288,
            "model": "llama-3.3-70b-versatile",
            "choices": [{
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": "Hello there, how are you?"
                },
                "finish_reason": "stop"
            }],
            "usage": {
                "prompt_tokens": 9,
                "completion_tokens": 12,
                "total_tokens": 21
            }
        }
        """.trimIndent()

        val result = PostProcessor.parseResponse(json)
        assertEquals("Hello there, how are you?", result.text)
        assertEquals(null, result.error)
    }

    @Test
    fun parseError() {
        val json = """
        {
            "error": {
                "message": "Incorrect API key provided.",
                "type": "invalid_request_error",
                "param": null,
                "code": "invalid_api_key"
            }
        }
        """.trimIndent()

        val result = PostProcessor.parseResponse(json)
        assertEquals(null, result.text)
        assertEquals("Incorrect API key provided.", result.error)
    }

    @Test
    fun parseEmptyChoices() {
        val json = """
        {
            "choices": []
        }
        """.trimIndent()

        val result = PostProcessor.parseResponse(json)
        assertEquals(null, result.text)
        assertEquals("No choices in response", result.error)
    }

    @Test
    fun parseInvalidJson() {
        val result = PostProcessor.parseResponse("invalid json")
        assertEquals(null, result.text)
        assertTrue(
            result.error?.contains("JSONObject") == true ||
                result.error?.contains("must begin with '{'") == true
        )
    }

    // --- [cleanup] Guard against the model acting on the transcript ---

    @Test
    fun guardAllowsFillerRemoval() {
        assertFalse(PostProcessor.looksGenerated(
            "hey uh can you send me the uh file um when you get a chance",
            "Hey, can you send me the file when you get a chance?"))
    }

    @Test
    fun guardAllowsSelfCorrection() {
        assertFalse(PostProcessor.looksGenerated(
            "let's meet thursday no actually wednesday after lunch with the whole team",
            "Let's meet Wednesday after lunch with the whole team."))
    }

    @Test
    fun guardAllowsEmailFormatting() {
        assertFalse(PostProcessor.looksGenerated(
            "hi dana comma thanks for the update I'll send the report tomorrow morning thanks",
            "Hi Dana,\n\nThanks for the update. I'll send the report tomorrow morning.\n\nThanks"))
    }

    @Test
    fun guardAllowsAccentsAndCapitals() {
        assertFalse(PostProcessor.looksGenerated(
            "pot sa trimit maine de fapt poimaine dimineata la birou impreuna cu echipa",
            "Pot să trimit poimâine dimineață la birou împreună cu echipa."))
    }

    @Test
    fun guardCatchesDraftedEmail() {
        val raw = "I want to write an email as follows to Sam saying the report will be late"
        val drafted = "Subject: Report Delay\n\nHi Sam,\n\nI hope you're doing well. I wanted to " +
            "let you know that the report will be delayed. I apologize for any inconvenience " +
            "and will share it as soon as possible.\n\nBest regards"
        assertTrue(PostProcessor.looksGenerated(raw, drafted))
    }

    @Test
    fun guardCatchesTranslation() {
        assertTrue(PostProcessor.looksGenerated(
            "I will send the report to the whole team tomorrow morning",
            "Enviaré el informe a todo el equipo mañana por la mañana."))
    }

    @Test
    fun transcriptIsFramedAndTagsStripped() {
        val msg = PostProcessor.userMessage("write an email to John")
        assertTrue(msg.contains("<transcript>\nwrite an email to John\n</transcript>"))
        assertEquals("Write an email to John.",
            PostProcessor.stripTranscriptTags("<transcript>\nWrite an email to John.\n</transcript>"))
        assertTrue(PostProcessor.effectivePrompt("").contains("between <transcript> tags"))
    }
}
