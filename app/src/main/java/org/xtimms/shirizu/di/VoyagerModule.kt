package org.xtimms.shirizu.di

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import org.xtimms.shirizu.sections.details.DetailsScreenModel
import org.xtimms.shirizu.sections.explore.catalog.CatalogScreenModel
import org.xtimms.shirizu.sections.explore.sources.SourcesScreenModel
import org.xtimms.shirizu.sections.feed.FeedScreenModel
import org.xtimms.shirizu.sections.library.history.HistoryScreenModel
import org.xtimms.shirizu.sections.library.shelves.ShelvesScreenModel
import org.xtimms.shirizu.sections.list.MangaListScreenModel
import org.xtimms.shirizu.sections.search.SearchScreenModel
import org.xtimms.shirizu.sections.settings.backup.RestoreBackupScreenModel
import org.xtimms.shirizu.sections.settings.shelf.categories.CategoriesScreenModel
import org.xtimms.shirizu.sections.settings.storage.StorageScreenModel
import org.xtimms.shirizu.sections.shelf.ShelfScreenModel
import org.xtimms.shirizu.sections.suggestions.SuggestionsScreenModel

@Module
@InstallIn(SingletonComponent::class)
interface VoyagerModule {

    @Binds
    @IntoMap
    @ScreenModelKey(ShelfScreenModel::class)
    fun bindShelfScreenModel(shelfScreenModel: ShelfScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(HistoryScreenModel::class)
    fun bindHistoryScreenModel(historyScreenModel: HistoryScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(SearchScreenModel::class)
    fun bindSearchScreenModel(searchScreenModel: SearchScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(SuggestionsScreenModel::class)
    fun bindSuggestionsScreenModel(suggestionsScreenModel: SuggestionsScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(SourcesScreenModel::class)
    fun bindSourcesScreenModel(sourcesScreenModel: SourcesScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(CatalogScreenModel::class)
    fun bindCatalogScreenModel(catalogScreenModel: CatalogScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(FeedScreenModel::class)
    fun bindFeedScreenModel(feedScreenModel: FeedScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(StorageScreenModel::class)
    fun bindStorageScreenModel(storageScreenModel: StorageScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(CategoriesScreenModel::class)
    fun bindCategoriesScreenModel(categoriesScreenModel: CategoriesScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelKey(ShelvesScreenModel::class)
    fun bindShelvesScreenModel(shelvesScreenModel: ShelvesScreenModel): ScreenModel

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(RestoreBackupScreenModel.Factory::class)
    fun bindRestoreBackupScreenModel(
        restoreBackupScreenModelFactory: RestoreBackupScreenModel.Factory
    ): ScreenModelFactory

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(MangaListScreenModel.Factory::class)
    fun bindMangaListScreenModel(
        mangaListScreenModelFactory: MangaListScreenModel.Factory
    ): ScreenModelFactory

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(DetailsScreenModel.Factory::class)
    fun bindDetailsScreenModelFactory(
        detailsScreenModelFactory: DetailsScreenModel.Factory
    ): ScreenModelFactory

}