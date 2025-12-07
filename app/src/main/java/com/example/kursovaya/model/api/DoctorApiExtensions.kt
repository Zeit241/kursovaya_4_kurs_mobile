package com.example.kursovaya.model.api

/**
 * Конвертирует base64 строку в data URI для использования с Glide
 * Если строка уже является валидным URL (начинается с http:// или https://), возвращает её без изменений
 * Если строка является base64 (начинается с "/9j/" для JPEG), добавляет префикс data URI
 */
fun String?.toImageDataUri(): String {
    if (this.isNullOrEmpty()) return ""
    
    // Если это уже URL, возвращаем как есть
    if (startsWith("http://") || startsWith("https://")) {
        return this
    }
    
    // Если это base64 JPEG (начинается с "/9j/"), добавляем префикс
    if (startsWith("/9j/") || startsWith("iVBORw0KGgo")) {
        // Определяем тип изображения по префиксу
        val mimeType = when {
            startsWith("/9j/") -> "image/jpeg"
            startsWith("iVBORw0KGgo") -> "image/png"
            else -> "image/jpeg" // По умолчанию JPEG
        }
        return "data:$mimeType;base64,$this"
    }
    
    // Если строка не пустая, но не распознана, пробуем как base64 JPEG
    return "data:image/jpeg;base64,$this"
}




