package com.lge.kotlinstudyapp.usecase

import com.lge.kotlinstudyapp.repo.Repo
import javax.inject.Inject

class ILPUseCase @Inject constructor(private val repo: Repo) {

    fun getItemListFlow() = repo.getItemListFlow()
}