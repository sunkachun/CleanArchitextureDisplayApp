package com.example.aslcodingtestproject.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DispatchersImpl : DispatchersProvider {
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val io: CoroutineDispatcher  get() = Dispatchers.IO
    override val default: CoroutineDispatcher  get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher  get() = Dispatchers.Unconfined
}