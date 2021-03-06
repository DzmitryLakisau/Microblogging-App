package by.d1makrat.microblogging_app.presenter.fragment

import by.d1makrat.microblogging_app.workers.FirebaseAuthenticationWorker
import by.d1makrat.microblogging_app.workers.FirebaseRealtimeDatabaseWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AddPresenter {

    private val MAX_COUNT_SYMBOLS = 100
    private var view: AddPresenter.View? = null

    fun detachView() {
        view = null
    }

    fun attachView(view: AddPresenter.View) {
        this.view = view
    }

    fun addPost(body: String){
        if (body.length > MAX_COUNT_SYMBOLS) {
            view?.showOverLoadError()
        }
        else {
            val firebaseRealtimeDatabaseWorker = FirebaseRealtimeDatabaseWorker()

            val uid = FirebaseAuthenticationWorker().getCurrentUserUid()

            firebaseRealtimeDatabaseWorker.getCurrentUserInfo(uid!!)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable{
                                user -> firebaseRealtimeDatabaseWorker.addPost(user, body, System.currentTimeMillis()/1000)
                            }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                view?.successfullyAdded()
                            },
                            {
                                error -> view?.showError(error.message)
                            }
                    )
        }
    }

    interface View{
        fun showError(message: String?)
        fun showOverLoadError()
        fun successfullyAdded()
    }
}
