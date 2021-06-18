package com.example.teashowcase.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.teashowcase.R
import com.example.teashowcase.databinding.FragmentGameBinding
import com.example.teashowcase.di.InjectingSavedStateViewModelFactory
import com.example.teashowcase.presentation.model.GameStateModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class GameFragment : DaggerFragment() {

    private val viewModel by viewModels<GameViewModel>()

    private val binding by viewBinding(FragmentGameBinding::bind)

    @Inject
    lateinit var abstractFactory: dagger.Lazy<InjectingSavedStateViewModelFactory>

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
        abstractFactory.get().create(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, ::renderViewState)
        with(binding) {
            checkAnswer.setOnClickListener { viewModel.onCheckAnswerClicked(answer = numberInput.text.toString()) }
            nextGame.setOnClickListener { viewModel.onNextGameClicked() }
        }
    }

    private fun renderViewState(state: GameStateModel) {
        with(binding) {
            answerTitle.isVisible = state.gameLoaded
            numberInput.isVisible = state.gameLoaded
            gameStatus.isVisible = state.gameLoaded
            gameStatus.text = state.gameStatus
            checkAnswer.isVisible = state.checkAnswerButtonVisible
            checkingAnswer.isVisible = state.checkingAnswerProgressVisible
            nextGame.isVisible = state.nextGameButtonVisible
            loadingGame.isVisible = state.loadingNextGameProgressVisible
        }
        state.gameLoadingError?.runIfNotHandled {
            showAlert(getString(R.string.next_game_loading_failed))
        }
        state.checkAnswerError?.runIfNotHandled {
            showAlert(getString(R.string.check_answer_failed))
        }
        state.gameState.newAnswerStatus?.runIfNotHandled {
            showAlert(state.gameState.answerStatus)
        }
    }

    private fun showAlert(text: CharSequence) {
        AlertDialog.Builder(requireActivity())
            .setMessage(text)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
