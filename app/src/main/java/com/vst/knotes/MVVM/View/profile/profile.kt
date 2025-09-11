package com.vst.knotes.MVVM.View.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.databinding.ProfileBinding
import com.vst.knotes.utils.SwipeButtonNew
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.FirebaseFirestore

class profile : BaseFragment<ProfileBinding>(), SwipeButtonNew.SwipeListener {
//class profile : BaseFragment<ProfileBinding>() {
    var IsActive : Boolean = false;
    val db = FirebaseFirestore.getInstance()
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): ProfileBinding {
        return ProfileBinding.inflate(inflater, parent, false)
    }

    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnprofile.setOnClickListener {
//            if (IsActive)
//                context?.let { stopVibration(it) }
//            else
//                context?.let { startVibration(it) }

            val user = hashMapOf(
                "first" to "Alan",
                "middle" to "Mathison",
                "last" to "Turing",
                "born" to 1912
            )

            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
//            val bitmap: Bitmap = loadSampleImage()  // Replace with your actual image loading method
//            (activity as? BaseActivity)?.runInference(bitmap)

//            (activity as? BaseActivity)?.replaceFragment(Category::class.java.newInstance() as Fragment)


//            (activity as? BaseActivity)?.showCustomDialog(
//                title = "Delete Item",
//                message = "Are you sure you want to delete this item?",
//                onYesClick = {
//                    // Handle Yes action
//                    Toast.makeText(requireContext(), "Item Deleted", Toast.LENGTH_SHORT).show()
//                },
//                onNoClick = {
//                    // Handle No action
//                    Toast.makeText(requireContext(), "Canceled", Toast.LENGTH_SHORT).show()
//                }
//            )
        }
//        val swipe_btn: SwipeButtonNew = view.findViewById(R.id.swipe_btn)
        val swipe_btn: SwipeButtonNew = binding.swipeBtn;
        swipe_btn.setButtonText("Swipe to Proceed")
        swipe_btn.setSwipeListener(this)

        // Add Ludo game button
        binding.btnLudoGame.setOnClickListener {
            launchLudoGame()
        }
    }

    fun startVibration(context: Context) {
//        val startIntent = Intent(context, VibrationService::class.java)
//        startIntent.putExtra("action", "start")
//        context.startService(startIntent)
    }

    // Function to stop the vibration service
    fun stopVibration(context: Context) {
//        val stopIntent = Intent(context, VibrationService::class.java)
//        stopIntent.putExtra("action", "stop")
//        context.startService(stopIntent)
    }

    override fun onSwipeSuccess() {
        (activity as BaseActivity).showCustomDialog(
            "Alert", "Swipe success", "Ok", "", {
                (activity as BaseActivity).supportFragmentManager.popBackStack()
            }, {
                // No click handler (if needed)
            })
    }

    // New method to launch the Ludo game
    private fun launchLudoGame() {
        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()
        val gameRef = database.getReference("ludo_game")

        // Initialize Ludo game
        val ludoGame = LudoGame(gameRef)
        ludoGame.startGame()

        // Example: Roll dice and move token
        val diceRoll = ludoGame.rollDice()
        ludoGame.moveToken(diceRoll)

        // Display game status
        Toast.makeText(requireContext(), "Ludo Game: Dice Rolled $diceRoll", Toast.LENGTH_SHORT).show()

        // Example: Check for win
        if (ludoGame.checkWin()) {
            Toast.makeText(requireContext(), "Player ${ludoGame.currentPlayer + 1} Wins!", Toast.LENGTH_SHORT).show()
        }
    }

    // Ludo Game class
    private class LudoGame(private val gameRef: DatabaseReference) {
        public var currentPlayer = 0
        private val players = 4
        private val tokensPerPlayer = 4
        private val boardSize = 52
        private val playerPositions = Array(players) { IntArray(tokensPerPlayer) { -1 } }
        private val homePositions = intArrayOf(0, 13, 26, 39) // Home positions for each player

        init {
            // Listen for game state changes
            gameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Update game state from Firebase
                    val gameState = snapshot.getValue(GameState::class.java)
                    if (gameState != null) {
                        currentPlayer = gameState.currentPlayer
                        for (i in 0 until players) {
                            for (j in 0 until tokensPerPlayer) {
                                playerPositions[i][j] = gameState.playerPositions[i][j]
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        fun startGame() {
            // Initialize game state
            currentPlayer = 0
            for (i in 0 until players) {
                for (j in 0 until tokensPerPlayer) {
                    playerPositions[i][j] = -1
                }
            }
            // Sync game state to Firebase
            syncGameState()
        }

        fun rollDice(): Int {
            return (1..6).random()
        }

        fun moveToken(diceRoll: Int) {
            // Move the first token of the current player
            if (playerPositions[currentPlayer][0] == -1) {
                playerPositions[currentPlayer][0] = homePositions[currentPlayer]
            } else {
                playerPositions[currentPlayer][0] = (playerPositions[currentPlayer][0] + diceRoll) % boardSize
            }
            currentPlayer = (currentPlayer + 1) % players
            // Sync game state to Firebase
            syncGameState()
        }

        fun checkWin(): Boolean {
            // Check if any player has all tokens at the end of the board
            for (i in 0 until players) {
                var allTokensHome = true
                for (j in 0 until tokensPerPlayer) {
                    if (playerPositions[i][j] != boardSize - 1) {
                        allTokensHome = false
                        break
                    }
                }
                if (allTokensHome) return true
            }
            return false
        }

        private fun syncGameState() {
            val gameState = GameState(currentPlayer, playerPositions)
            gameRef.setValue(gameState)
        }
    }

    // Data class for Firebase
    data class GameState(
        val currentPlayer: Int = 0,
        val playerPositions: Array<IntArray> = Array(4) { IntArray(4) { -1 } }
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as GameState
            if (currentPlayer != other.currentPlayer) return false
            if (!playerPositions.contentDeepEquals(other.playerPositions)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = currentPlayer
            result = 31 * result + playerPositions.contentDeepHashCode()
            return result
        }
    }

//    fun loadSampleImage(): Bitmap {
//        val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.sample_img_model) }
//        if (drawable is BitmapDrawable) {
//            return drawable.bitmap
//        } else {
//            throw IllegalArgumentException("Drawable resource is not a BitmapDrawable.")
//        }
//    }

}