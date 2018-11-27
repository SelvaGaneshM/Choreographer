package zachinio.choreograper.animation

import android.view.View
import android.view.animation.AnimationUtils
import io.reactivex.Completable
import zachinio.choreograper.Choreographer
import zachinio.choreograper.R
import java.lang.ref.WeakReference

internal class FadeAnimation(
    private val viewWeak: WeakReference<View>,
    private val direction: Choreographer.Direction,
    private val animationType: Choreographer.AnimationType,
    private val duration: Long
) : Animation() {

    override fun animate(): Completable {
        setVisibilityState(false)
        return Completable.create {
            val scaleAnimation = getFadeAnimation()
            scaleAnimation?.duration = duration
            scaleAnimation?.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationRepeat(p0: android.view.animation.Animation?) {

                }

                override fun onAnimationStart(p0: android.view.animation.Animation?) {

                }

                override fun onAnimationEnd(p0: android.view.animation.Animation?) {
                    it.onComplete()
                }
            })
            viewWeak.get()?.startAnimation(scaleAnimation)
            setVisibilityState(true)
        }
    }

    private fun getFadeAnimation(): android.view.animation.Animation? {
        return when (direction) {
            Choreographer.Direction.IN -> {
                viewWeak.get()?.visibility = View.INVISIBLE
                AnimationUtils.loadAnimation(viewWeak.get()?.context, R.anim.fade_in)
            }
            Choreographer.Direction.OUT -> {
                viewWeak.get()?.visibility = View.VISIBLE
                AnimationUtils.loadAnimation(viewWeak.get()?.context, R.anim.fade_out)
            }
            else -> throw IllegalStateException(direction.name + " can't be used with " + animationType.name)
        }
    }

    private fun setVisibilityState(isEndingState: Boolean) {
        when (direction) {
            Choreographer.Direction.IN -> viewWeak.get()?.visibility =
                    if (isEndingState) View.VISIBLE else View.INVISIBLE
            Choreographer.Direction.OUT -> viewWeak.get()?.visibility =
                    if (isEndingState) View.INVISIBLE else View.VISIBLE
            else -> {
                throw IllegalStateException(direction.name + " can't be used with " + animationType.name)
            }
        }
    }
}