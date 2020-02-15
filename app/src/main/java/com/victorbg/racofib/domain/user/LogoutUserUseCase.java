package com.victorbg.racofib.domain.user;

import androidx.work.WorkManager;

import com.victorbg.racofib.data.database.AppDatabase;
import com.victorbg.racofib.domain.UseCase;
import com.victorbg.racofib.data.repository.AppExecutors;
import com.victorbg.racofib.data.repository.RepositoryCleaner;
import com.victorbg.racofib.data.repository.base.Repository;
import com.victorbg.racofib.data.preferences.PrefManager;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LogoutUserUseCase extends UseCase<Void, Void> {

  private final RepositoryCleaner cleaner;
  private final AppDatabase appDatabase;
  private final PrefManager prefManager;

  @Inject
  public LogoutUserUseCase(
      AppExecutors appExecutors,
      RepositoryCleaner cleaner,
      AppDatabase appDatabase,
      PrefManager prefManager) {
    super(appExecutors);
    this.cleaner = cleaner;
    this.appDatabase = appDatabase;
    this.prefManager = prefManager;
  }

  /**
   * Logs out the user from the system deleting all the data from the databases and cleaning all the
   * {@link Repository} to indicates that a new data is going to be added and they have to fetch it
   * independently of the current state of their cached data.
   *
   * @return null
   */
  @Override
  public Void execute() {
    WorkManager.getInstance().cancelUniqueWork("SeasonCheckTask");
    appDatabase.runInTransaction(appDatabase::clearAllTables);
    cleaner.clean();
    prefManager.logout();
    return null;
  }
}
