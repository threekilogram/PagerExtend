package tech.threekilogram.viewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import java.lang.ref.WeakReference;
import tech.threekilogram.viewpager.adapter.MaxCountAdapter;
import tech.threekilogram.viewpager.observer.OnPagerScrollListener;
import tech.threekilogram.viewpager.observer.PagerScroll;

/**
 * @author LiuJin
 * @date 2017-12-25
 *     轮播图,包含一个LoopViewPager,也可以配置指示器
 */
public class BannerView extends FrameLayout {

      /**
       * pager
       */
      private ExtendViewPager            mViewPager;
      /**
       * adapter has max count
       */
      private MaxCountAdapter            mMaxCountAdapter;
      /**
       * start loop
       */
      private BannerOnPageChangeListener mOnPagerScrollListener;
      /**
       * help loop
       */
      private LoopHandler                mLoopHandler;
      /**
       * true is looping
       */
      private boolean                    isAutoLoop;
      /**
       * loop time
       */
      private int                        mLoopTime;
      /**
       * pager scroll
       */
      private PagerScroll                mPagerScroll;

      public BannerView ( @NonNull Context context ) {

            this( context, null );
      }

      public BannerView ( @NonNull Context context, @Nullable AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public BannerView (
          @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init();
      }

      /**
       * init field
       */
      private void init ( ) {

            mViewPager = new ExtendViewPager( getContext() );
            mOnPagerScrollListener = new BannerOnPageChangeListener();
            mLoopHandler = new LoopHandler( this );
      }

      @Override
      protected void onFinishInflate ( ) {

            super.onFinishInflate();

            /* 添加pager */
            addView( mViewPager, 0 );
            mViewPager.addOnPageChangeListener( mOnPagerScrollListener );

            /* 支持pager裁剪 */
            setClipChildren( false );
            setClipToPadding( false );
            mViewPager.setClip( false );
      }

      @Override
      public boolean dispatchTouchEvent ( MotionEvent ev ) {

            /* cancel loop when touch */
            mLoopHandler.clearLoopToNextAtDelayed();

            /* start loop when finger up if in looping state */
            int action = ev.getAction();
            if( action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_OUTSIDE ) {

                  if( isAutoLoop ) {
                        mLoopHandler.loopToNextAtDelayed( mLoopTime );
                  }
            }

            return super.dispatchTouchEvent( ev );
      }

      @Override
      protected void onDetachedFromWindow ( ) {

            super.onDetachedFromWindow();
            /* release */
            mLoopHandler.removeCallbacksAndMessages( null );
      }

      /**
       * 获取banner中pager
       */
      public ExtendViewPager getViewPager ( ) {

            return mViewPager;
      }

      /**
       * 为banner设置pager adapter
       */
      public void setPagerAdapter ( PagerAdapter pagerAdapter ) {

            mMaxCountAdapter = new MaxCountAdapter( pagerAdapter );
            mViewPager.setAdapter( mMaxCountAdapter );
            mViewPager.setCurrentItem( mMaxCountAdapter.getStartPosition() );
      }

      /**
       * 获取设置的pager adapter
       */
      public PagerAdapter getPagerAdapter ( ) {

            return mMaxCountAdapter == null ? null : mMaxCountAdapter.getPagerAdapter();
      }

      /**
       * 增加滚动时间
       */
      public void addScrollDuration ( int addTime ) {

            mViewPager.setDurationAdded( addTime );
      }

      /**
       * 滑动到下一个页面
       */
      public void loopToNextPage ( ) {

            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem( currentItem + 1, true );
      }

      /**
       * true : 正在loop
       */
      public boolean isAutoLoop ( ) {

            return isAutoLoop;
      }

      /**
       * 开始loop
       */
      public void startLoop ( ) {

            startLoop( 2400 );
      }

      /**
       * 开始loop
       *
       * @param loopTime loop间隔
       */
      public void startLoop ( int loopTime ) {

            if( isAutoLoop ) {
                  mLoopTime = loopTime;
            } else {
                  isAutoLoop = true;
                  mLoopTime = loopTime;
                  mLoopHandler.loopToNextAtDelayed( loopTime );
            }
      }

      /**
       * 结束loop
       */
      public void stopLoop ( ) {

            if( isAutoLoop ) {

                  isAutoLoop = false;
                  mLoopHandler.clearLoopToNextAtDelayed();
            }
      }

      /**
       * 为pager设置pageMargin
       *
       * @param unit 单位
       * @param marginSize 尺寸
       */
      public void setPageMargin ( int unit, float marginSize ) {

            float dimension = TypedValue.applyDimension(
                unit,
                marginSize,
                getResources().getDisplayMetrics()
            );
            setPageMargin( (int) dimension );
      }

      /**
       * 为pager设置pageMargin
       */
      public void setPageMargin ( int marginPixels ) {

            mViewPager.setPageMargin( marginPixels );
      }

      /**
       * 设置滚动方向监听
       */
      public void setOnPagerScrollListener ( OnPagerScrollListener onPagerScrollListener ) {

            if( mPagerScroll == null ) {
                  mPagerScroll = new PagerScroll( mViewPager );
            }
            if( onPagerScrollListener == null ) {
                  mPagerScroll.setOnPagerScrollListener( null );
            } else {
                  mPagerScroll.setOnPagerScrollListener(
                      new BannerOnPagerScrollListener( onPagerScrollListener )
                  );
            }
      }

      /**
       * 获取设置的滚动监听
       */
      public OnPagerScrollListener getOnPagerScrollListener ( ) {

            if( mPagerScroll == null ) {
                  return null;
            }
            return mPagerScroll.getOnPagerScrollListener();
      }

      /**
       * 辅助发送延时消息
       */
      private static class LoopHandler extends Handler {

            private static final int WHAT_LOOP = 1569;

            private WeakReference<BannerView> mRef;
            private boolean                   isCleared;

            LoopHandler ( BannerView banner ) {

                  mRef = new WeakReference<>( banner );
            }

            private void loopToNextAtDelayed ( int delayed ) {

                  sendEmptyMessageDelayed( WHAT_LOOP, delayed );
                  isCleared = false;
            }

            private void clearLoopToNextAtDelayed ( ) {

                  if( isCleared ) {
                        return;
                  }
                  removeMessages( WHAT_LOOP );
                  isCleared = true;
            }

            @Override
            public void handleMessage ( Message msg ) {

                  BannerView bannerView = mRef.get();
                  if( bannerView == null ) {
                        return;
                  }

                  if( msg.what == WHAT_LOOP ) {
                        bannerView.loopToNextPage();
                  }
            }
      }

      /**
       * 连接{@link ExtendViewPager}和{@link LoopHandler}
       */
      private class BannerOnPageChangeListener implements ViewPager.OnPageChangeListener {

            BannerOnPageChangeListener ( ) { }

            @Override
            public void onPageScrolled (
                int position, float positionOffset, int positionOffsetPixels ) { }

            @Override
            public void onPageSelected ( int position ) {

                  if( isAutoLoop ) {
                        mLoopHandler.loopToNextAtDelayed( mLoopTime );
                  }
            }

            @Override
            public void onPageScrollStateChanged ( int state ) { }
      }

      /**
       * 观察滚动,辅助转换position为正确位置
       */
      private class BannerOnPagerScrollListener implements OnPagerScrollListener {

            /**
             * 用户设置的监听
             */
            private OnPagerScrollListener mOnPagerScrollListener;

            private BannerOnPagerScrollListener (
                OnPagerScrollListener onPagerScrollListener ) {

                  mOnPagerScrollListener = onPagerScrollListener;
            }

            @Override
            public void onCurrent ( int currentPosition, float offset ) {

                  if( mOnPagerScrollListener != null ) {
                        int position = mMaxCountAdapter.getAdapterPosition( currentPosition );
                        mOnPagerScrollListener.onCurrent( position, offset );
                  }
            }

            @Override
            public void onNext ( int nextPosition, float offset ) {

                  if( mOnPagerScrollListener != null ) {
                        int position = mMaxCountAdapter.getAdapterPosition( nextPosition );
                        mOnPagerScrollListener.onNext( position, offset );
                  }
            }

            @Override
            public void onPageSelected ( int position ) {

                  if( mOnPagerScrollListener != null ) {
                        position = mMaxCountAdapter.getAdapterPosition( position );
                        mOnPagerScrollListener.onPageSelected( position );
                  }
            }
      }
}