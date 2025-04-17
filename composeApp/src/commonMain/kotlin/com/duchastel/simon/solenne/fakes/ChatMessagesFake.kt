package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.ChatMessage
import com.duchastel.simon.solenne.data.MessageAuthor.AI
import com.duchastel.simon.solenne.data.MessageAuthor.User

object ChatMessagesFake {
    val chatMessages = listOf(
        ChatMessage(text = "Hi, how can I help you?", author = AI, id = "1"),
        ChatMessage(text = "I've been having trouble sleeping lately. Any suggestions?", author = User, id = "2"),
        ChatMessage(text = "I'm sorry to hear that. Have you tried establishing a regular sleep schedule?", author = AI, id = "3"),
        ChatMessage(text = "Yes, I try to go to bed at the same time every night.", author = User, id = "4"),
        ChatMessage(text = "That's good. What about avoiding screens before bedtime?", author = AI, id = "5"),
        ChatMessage(text = "I usually watch TV or check my phone before sleeping. Is that bad?", author = User, id = "6"),
        ChatMessage(text = "The blue light from screens can interfere with melatonin production. Try avoiding screens 1-2 hours before bed.", author = AI, id = "7"),
        ChatMessage(text = "I didn't know that. I'll try to read a book instead.", author = User, id = "8"),
        ChatMessage(text = "Reading is excellent! Also consider keeping your bedroom cool and dark.", author = AI, id = "9"),
        ChatMessage(text = "My room does get pretty warm at night. I'll try adjusting the temperature.", author = User, id = "10"),
        ChatMessage(text = "Perfect. What about your caffeine intake during the day?", author = AI, id = "11"),
        ChatMessage(text = "I usually have coffee until about 4pm. Is that too late?", author = User, id = "12"),
        ChatMessage(text = "Ideally, avoid caffeine after noon. It can stay in your system for up to 8 hours.", author = AI, id = "13"),
        ChatMessage(text = "That's longer than I thought! I'll cut back on afternoon coffee.", author = User, id = "14"),
        ChatMessage(text = "Great plan. How about exercise? Regular physical activity can improve sleep quality.", author = AI, id = "15"),
        ChatMessage(text = "I work out in the evenings. Is that good timing?", author = User, id = "16"),
        ChatMessage(text = "Try to finish exercising at least 3 hours before bedtime. Morning or afternoon workouts are ideal for sleep.", author = AI, id = "17"),
        ChatMessage(text = "I'll see if I can shift my schedule. Any relaxation techniques you'd recommend?", author = User, id = "18"),
        ChatMessage(text = "Deep breathing, progressive muscle relaxation, or meditation can all help calm your mind before sleep.", author = AI, id = "19"),
        ChatMessage(text = "I've never tried meditation. Any tips for beginners?", author = User, id = "20"),
        ChatMessage(text = "Start with just 5 minutes of focusing on your breath. There are also great guided meditation apps available.", author = AI, id = "21"),
        ChatMessage(text = "That sounds manageable. I'll download an app today.", author = User, id = "22"),
        ChatMessage(text = "Wonderful! Another suggestion is to use your bed only for sleep, not for working or watching TV.", author = AI, id = "23"),
        ChatMessage(text = "I do tend to work from bed sometimes. I'll stop that habit.", author = User, id = "24"),
        ChatMessage(text = "Great! Your brain will start to associate your bed with sleep rather than alertness.", author = AI, id = "25"),
        ChatMessage(text = "Makes sense. What about naps during the day?", author = User, id = "26"),
        ChatMessage(text = "Short naps (20-30 minutes) before 3pm are fine, but avoid longer naps or napping later in the day.", author = AI, id = "27"),
        ChatMessage(text = "Got it. What about diet? Does what I eat affect my sleep?", author = User, id = "28"),
        ChatMessage(text = "Absolutely! Avoid heavy meals close to bedtime. Try to finish eating 2-3 hours before sleep.", author = AI, id = "29"),
        ChatMessage(text = "And alcohol? I sometimes have a nightcap to help me sleep.", author = User, id = "30"),
        ChatMessage(text = "While alcohol might help you fall asleep, it reduces sleep quality and disrupts REM sleep.", author = AI, id = "31"),
        ChatMessage(text = "I didn't realize that. I'll skip the nightcap.", author = User, id = "32"),
        ChatMessage(text = "Good choice. What about your bedroom environment? Is it comfortable?", author = AI, id = "33"),
        ChatMessage(text = "I think so, but my mattress is quite old.", author = User, id = "34"),
        ChatMessage(text = "An uncomfortable mattress can definitely impact sleep quality. Consider investing in a new one if possible.", author = AI, id = "35"),
        ChatMessage(text = "I've been thinking about it. Any recommendations for mattress type?", author = User, id = "36"),
        ChatMessage(text = "It depends on your preferences - memory foam, innerspring, hybrid, etc. Test different types if you can.", author = AI, id = "37"),
        ChatMessage(text = "I'll do some research and testing. What about pillows?", author = User, id = "38"),
        ChatMessage(text = "A good pillow that properly supports your neck is important. Replace pillows every 1-2 years.", author = AI, id = "39"),
        ChatMessage(text = "Mine are definitely older than that! I'll get new ones.", author = User, id = "40"),
    )
}
