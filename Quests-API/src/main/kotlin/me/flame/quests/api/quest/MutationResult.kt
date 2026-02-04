package me.flame.quests.api.quest

sealed class MutationResult {
    object NoChange : MutationResult()
    object Progressed : MutationResult()
    object Completed : MutationResult()
}
