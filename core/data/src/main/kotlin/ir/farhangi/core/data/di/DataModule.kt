package ir.farhangi.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.farhangi.core.data.repository.AuthRepository
import ir.farhangi.core.data.repository.BookRepository
import ir.farhangi.core.data.repository.CourseRepository
import ir.farhangi.core.data.repository.DefaultAuthRepository
import ir.farhangi.core.data.repository.DefaultBookRepository
import ir.farhangi.core.data.repository.DefaultCourseRepository
import ir.farhangi.core.data.repository.DefaultMagazineRepository
import ir.farhangi.core.data.repository.DefaultSearchRepository
import ir.farhangi.core.data.repository.DefaultUserRepository
import ir.farhangi.core.data.repository.MagazineRepository
import ir.farhangi.core.data.repository.SearchRepository
import ir.farhangi.core.data.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: DefaultAuthRepository): AuthRepository

    @Binds @Singleton
    abstract fun bindBookRepository(impl: DefaultBookRepository): BookRepository

    @Binds @Singleton
    abstract fun bindCourseRepository(impl: DefaultCourseRepository): CourseRepository

    @Binds @Singleton
    abstract fun bindMagazineRepository(impl: DefaultMagazineRepository): MagazineRepository

    @Binds @Singleton
    abstract fun bindSearchRepository(impl: DefaultSearchRepository): SearchRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: DefaultUserRepository): UserRepository
}