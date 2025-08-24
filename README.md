# LobstaChat

LobstaChat is a private, offline-first AI chat application for Android, powered by `llama.cpp`. It allows you to run powerful language models directly on your device, ensuring your conversations remain private and accessible without an internet connection.

## Features

- **Offline-First:** All your data is stored locally on your device. No internet connection is required for the core chat functionality.
- **Private:** Your conversations never leave your device.
- **Run Large Language Models (LLMs):** Supports running GGUF-formatted LLMs locally.
- **Text-to-Speech (TTS):** Have the assistant's responses read aloud to you.
- **Speech-to-Text (STT):** Dictate your messages instead of typing.
- **Seamless Voice Conversations:** Engage in a continuous, hands-free voice conversation with the AI.
- **Efficient Context Management:** The app uses session caching to quickly restore chat context, avoiding the need to re-process long conversations.
- **Automatic Summarization:** For very long conversations, the app automatically summarizes the history to maintain context indefinitely without overflowing the context window.
- **Tasks:** Create simple chat templates with a pre-defined system prompt and model for quick, recurring actions.
- **Personas:** Create and manage advanced AI personas with custom system prompts and detailed inference parameters (temperature, top-k, top-p) to tailor the AI's personality and responses.
- **Chat Management:** Organize your chats into folders, edit chat history, and more.

## Getting Started

1.  **Download a Model:** The app requires a GGUF-formatted language model. You can download one from sources like Hugging Face.
2.  **Import the Model:** Use the "Add New Model" feature in the app to import the downloaded `.gguf` file.
3.  **Start Chatting:** Once the model is imported, you can start a new chat and begin your conversation.

## Advanced Features

### Personas

Personas allow you to create customized AI personalities. You can define a name, a detailed system prompt, and specific inference parameters (like temperature, top-k, etc.) to control the AI's behavior. This is great for creating specialized assistants, like a coding expert, a creative writer, or a travel guide.

To create a persona:
1.  Open the navigation drawer and select "Manage Personas".
2.  Click the "Add New Persona" button.
3.  Fill in the details for your persona and save it.

### Voice Conversations

You can have a hands-free voice conversation with the AI.
1.  Open a chat.
2.  Tap the "headphones" icon in the message input bar to start the voice conversation mode.
3.  The app will start listening for your voice.
4.  After you speak, the app will send your message to the model.
5.  The model's response will be spoken aloud, and the app will automatically start listening for your next input.
6.  Tap the "headphones" icon again to stop the voice conversation mode.

## Contributing

This project is open source. Contributions are welcome! Please feel free to open an issue or submit a pull request.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.
