# 如果您觉得本项目对你有用，请随手star，谢谢


Filmr - 轻松的视频编辑」是一款为业余爱好者设计的视频编辑软件，你不需要专业的摄影知识，也能创造出独一无二的视频。在 Filmr，你能直接拖动进度条来裁剪素材，长按某条素材，你可以改变该素材的播放顺序。另外，右划还能删除、复制或者倒序播放视频素材，
iOS版本在appStore中可以直接下载，但是没有android版本，现在刚好有时间就把filmr的android版本写了出来，可供参考。
整个app主要有的功能就是：视频裁切，视频合成，视频中添加文字，语音，图片，涂鸦等功能，使用起来也是超级的方便，直接秒杀其他收费类app。

下面只要对整个项目的一些难点讲解一下：
 * 1，这里裁切其实很大部分使用的是七牛短视频sdk，七牛裁切使用的是GLSurfaceView。而他在acivitiy中只能存在一个，并且需要渲染器，所以后面决定了使用fragment，在fragment里面渲染，后面再把PLShortVideoEditor传人到mianActivity
 * 2.切换fragment的时候，需要 mShortVideoEditor.stopPlayback(); 停止视频播放，不然切换会有声音继续
 * 3.需求要求连续播放不同视频，不能使用多个播放器，影响性能，但是可是使用fragment各自有各自的生命周期
 * 4.因为视频有裁切，合成是在最后执行，如果需要判断获取播放的时间点，以及裁切起始结束都应该使用秒为单位，防止偏移
 * 5.需要注意的地方，activty里面添加fragment，他们的生命周期是分开的，并行，并不会说执行了fragment之后再继续往下执行
 * 6.如果有多个状态需要判断，使用枚举比int整型，更加直观
 * 7.视频有多个，每一个需要需要单独判断，不能相加后再判断，比如说总时间 （int）4.5+5.2+5.8）和（int）4.8+（int）5.2+（int）5.8
 * 8.mShortVideoEditor.startPlayback();执行了之后再去执行其他添加编辑方法，七牛那边要求
 * 9.删除recyclerView一个item，再添加一个新的item，会出现旧的item的缓存,记得 mMenuRecyclerView.removeViewAt(position);
 * 10.关于在播放的时候，获取视频播放的时间，是以ms为单位还是以s单位，以ms 单位进度条会流畅很多，但是需要在指定时间点做多重判断，所以最后衡量了一下，使用了0.1秒
 * 11.视频合成的时候，需求要求是能够在指定的点插入音频，而且可以插入多段音频（需要用到ffmpeg混音）


这里用到了好几个开源项目：
* 6.0权限判断 [项目地址](https://github.com/tangpeng/EsayPermissions)
*  封装recyclerview-swipes  [项目地址](https://github.com/yanzhenjie/SwipeRecyclerView)
* mp3recorder  [项目地址](https://github.com/GavinCT/AndroidMP3Recorder)

## Demo
![Demo](/pic/cut.gif)
![Demo](/pic/music.gif)
![Demo](/pic/wenzi.gif)
![Demo](/pic/tietu.gif)
