//package com.iyuba.conceptEnglish.han
//
//import android.media.MediaPlayer
//import android.media.MediaRecorder
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.DisplayMetrics
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.DialogFragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import com.iyuba.conceptEnglish.R
//import com.iyuba.conceptEnglish.databinding.CorrectSoundLayoutBinding
//import com.iyuba.conceptEnglish.han.utils.*
//import com.iyuba.conceptEnglish.sqlite.mode.VoaDetail
//import com.iyuba.conceptEnglish.util.ConceptApplication
//import com.iyuba.core.lil.user.UserInfoManager
//import com.iyuba.imooclib.ui.mobclass.MobClassActivity
//import java.io.File
//
//
///**
//苏州爱语吧科技有限公司
// */
//
//class CorrectSoundDialog : DialogFragment(), View.OnClickListener {
//    private lateinit var fileName: String
//    private val recorder by lazy { MediaRecorder() }
//    private lateinit var bind: CorrectSoundLayoutBinding
//    private var isRecording = false
//    private val concept by lazy { ViewModelProvider(requireActivity())[ConceptViewModel::class.java] }
//    private lateinit var player :MediaPlayer
//    private var videoUrl = ""
//    private var wordUrl = ""
//    private val userId = UserInfoManager.getInstance().userId.toString()
//    private lateinit var currentVoa: VoaDetail
//    private lateinit var voaId: String
//    private lateinit var groupId: String
//    private val handler=Handler(Looper.myLooper()!!){
//        when(it.what){
//            0->"加载失败".showToast()
//            1->"加载成功".showToast()
//        }
//        true
//    }
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        bind = DataBindingUtil.inflate(inflater, R.layout.correct_sound_layout, container, false)
//        return bind.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        bind.closeCorrectSound.setOnClickListener(this)
//        bind.playCorrect.setOnClickListener(this)
//        bind.listenOriginal.setOnClickListener(this)
//        bind.clickStart.setOnClickListener(this)
//        bind.seekLisa.setOnClickListener(this)
//        bind.wordScore.setOnClickListener(this)
//        bind.contentEvaluation.setOnWordClickListener(object : OnWordClickListener() {
//            override fun onNoDoubleClick(str: String) {
//                bind.title = str
//                loadWord(str)
//            }
//        })
//        player=MediaPlayer().apply {
//            setOnPreparedListener{start()}
//        }
//        fileName = "${requireContext().externalCacheDir?.absolutePath}audio_record_word.wav"
//        bind.wordScore.visibility = View.INVISIBLE
//    }
//
//    fun changeContent(bean: VoaDetail,currentWord:String) {
//        currentVoa = bean
//        bind.contentEvaluation.apply {
//            text = bean.readResult
//            val selectTextColor=ContextCompat.getColor(requireContext(),R.color.bookChooseUncheck)
//            setSelectTextBackColor(selectTextColor)
//        }
//        voaId = bean.realVoaId()
//        groupId = bean.lineN
//        val word= currentWord.ifEmpty {
//            bean.sentence.split(" ")[0]
//        }
//        bind.title = word
//        loadWord(word)
//    }
//
//    private fun loadWord(word: String) {
//        lifecycleScope.launchWhenCreated {
//            try {
//                val realWord=word.removeSymbol()
//                val helper = CorrectEvalHelper(requireContext())
//                val list = helper.findByContent(userId,voaId,groupId,realWord,true)
//                if (list.isNotEmpty()) {
//                    val result = concept.correctSound(realWord, list[0])
//                    activity?.runOnUiThread {
//                        bind.correctPronunciation.text = result.realOri
//                        bind.yourPronunciation.text = result.realUserPron
//                        val wordDefinition= resources.getString(R.string.word_definition)+result.def
//                        bind.wordDefinition.text = wordDefinition
//                        videoUrl = result.audio
//                        playVideo()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                handler.sendEmptyMessage(0)
//            }
//        }
//    }
//
//    override fun onClick(p0: View?) {
//        when (p0?.id) {
//            R.id.close_correct_sound -> dismiss()
//            R.id.play_correct -> playVideo()
//            R.id.listen_original -> playVideo()
//            R.id.word_score -> playVideo(wordUrl)
//            R.id.click_start -> clickStart()
//            R.id.seek_lisa -> {
//                val typeIdFilter= ArrayList<Int>()
//                typeIdFilter.add(3)
//                startActivity(MobClassActivity.buildIntent(activity, 3, true, typeIdFilter))
//            }
//        }
//    }
//    private fun clickStart(){
//        bind.clickStart.text=if (isRecording) {
//            "结束评测".showToast()
//            stopRecord()
//            resources.getString(R.string.click_start)
//        } else {
//            startRecording()
//            "开始评测".showToast()
//            resources.getString(R.string.click_stop)
//        }
//    }
//
//    private fun playVideo(url: String = videoUrl) {
//        if (url.isNotEmpty()&&!player.isPlaying) {
//            player.reset()
//            player.setDataSource(url)
//            player.prepareAsync()
//        }
//    }
//
//
//    private fun startRecording() {
//        recorder.apply {
//            setAudioSource(MediaRecorder.AudioSource.MIC)
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//            setOutputFile(fileName)
//            prepare()
//            start()
//            isRecording = true
//        }
//    }
//
//    private fun stopRecord() {
//        if (isRecording) {
//            isRecording = false
//            recorder.setOnErrorListener(null)
//            recorder.setOnInfoListener(null)
//            recorder.setPreviewDisplay(null)
//            recorder.stop()
//            recorder.reset()
//        }
//        lifecycleScope.launchWhenStarted {
//            try {
//                val file = File(fileName)
//                val result = concept.evaluationSentence(currentVoa,file,currentVoa.lineN).data
//                wordUrl = result.URL.changeVideoUrl()
//                bind.wordScore.visibility = View.VISIBLE
//                bind.wordScore.text = result.realScopes
//            } catch (e: Exception) {
//                "加载出错".showToast()
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val dw = dialog?.window
//        dw!!.setBackgroundDrawableResource(R.drawable.read_again) //一定要设置背景
//        val dm = DisplayMetrics()
//        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
//        val params = dw.attributes
//        //屏幕底部显示
//        params.gravity = Gravity.CENTER
//        //设置屏幕宽度高度
//        val heightPercentage=when (ConceptApplication.currentEvalLength) {
//            in (0..37) -> 2.1F
//            in (38..65) -> 1.9F
//            else -> 1.7F
//        }
//        params.width = (dm.widthPixels / 1.1f).toInt()//屏幕宽度
//        params.height = (dm.heightPixels / heightPercentage).toInt() //屏幕高度的1/3
//        dw.attributes = params
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        player.stop()
//        player.release()
//        handler.removeCallbacksAndMessages(null)
//    }
//}
//
//
