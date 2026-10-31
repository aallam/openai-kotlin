package com.aallam.openai.client.internal

import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.skill.Skill
import com.aallam.openai.api.skill.SkillId
import com.aallam.openai.api.skill.SkillListQuery
import com.aallam.openai.client.Skills
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

internal suspend fun awaitListedSkill(client: Skills, id: SkillId): Skill = withContext(Dispatchers.Default) {
    // List visibility can lag direct retrieval; use the same real-time budget as version availability.
    withTimeout(20_000) {
        var result = findListedSkill(client, id)
        while (result == null) {
            delay(250)
            result = findListedSkill(client, id)
        }
        result
    }
}

private suspend fun findListedSkill(client: Skills, id: SkillId): Skill? {
    var after: SkillId? = null
    val cursors = mutableSetOf<String>()
    while (true) {
        val page = client.skillsPage(SkillListQuery(after = after, limit = 100, order = SortOrder.Descending))
        page.find { it.id == id }?.let { return it }
        if (page.hasMore != true) return null
        val cursor = checkNotNull(page.lastId) { "Skills listing omitted the next-page cursor" }
        check(cursors.add(cursor)) { "Skills listing did not advance its cursor" }
        after = SkillId(cursor)
    }
}
