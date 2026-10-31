# Live audio fixtures

These excerpts retain complete speech phrases while limiting billed audio duration.
The source recordings are available in commit `425527fd3c6b7f9f67c18b1bb2ec6f146f881e49`.

| Fixture | Original interval | Duration | Retained speech |
| --- | --- | --- | --- |
| `micro-machines.wav` | 0.1-3.2 seconds | 3.1 seconds | The complete Micro Machine Man introduction used by the transcription assertion. |
| `multilingual.wav` | 1.0-10.2 seconds | 9.2 seconds | The first complete French sentence describing Whisper, translated by the translation assertion. |

Both cuts fall in silence and preserve the source PCM encoding, sample rate, and channels.
The seven transcription/translation requests submit 40 seconds in total, down from 245.76 seconds.
Text, verbose JSON, language, duration, segment, and word timestamp checks remain unchanged apart from expected durations.

To reproduce an excerpt, read the source WAV with Python's `wave` module, seek to
`round(start * framerate)`, retain `round((end - start) * framerate)` frames, and
write those frames with the original WAV parameters.
