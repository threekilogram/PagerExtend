package tech.threekilogram.pager.observer;

import android.support.v4.view.ViewPager;

/**
 * Created by LiuJin on 2017-12-31:8:45
 * viewPager滚动时的观察者,滚动时会通知 OnPagerScrollListener 当前滚动的position,下个将要显示的position,以及进度值
 *
 * @author liujin
 */
class OnPageChangeScrollListener implements ViewPager.OnPageChangeListener {

      /**
       * 用于获取当前pager状态
       */
      private ItemViewGroup         mItemViewGroup;
      /**
       * 当前滚动状态
       */
      private int                   mCurrentState;
      /**
       * 当前条目索引
       */
      private int                   mStartIndex;
      /**
       * 滚动时下一个条目索引
       */
      private int                   mNextIndex;
      /**
       * 是否象征方向滚动
       */
      private boolean               isLeft;
      /**
       * 滚动时回调
       */
      private OnPagerScrollListener mOnPagerScrollListener;

      /**
       * 创建
       *
       * @param pager pager
       */
      OnPageChangeScrollListener ( ItemViewGroup pager ) {

            mItemViewGroup = pager;
      }

      /**
       * 设置监听
       */
      void setOnPagerScrollListener ( OnPagerScrollListener onPagerScrollListener ) {

            mOnPagerScrollListener = onPagerScrollListener;
      }

      /**
       * 获取设置的监听
       */
      OnPagerScrollListener getOnPagerScrollListener ( ) {

            return mOnPagerScrollListener;
      }

      @Override
      public void onPageScrolled ( int position, float positionOffset, int positionOffsetPixels ) {

            if( mOnPagerScrollListener == null ) {
                  return;
            }

            if( mCurrentState == ViewPager.SCROLL_STATE_SETTLING ) {

                  final float endFlag = 0.f;

                  if( positionOffset == endFlag ) {
                        int itemPosition = mItemViewGroup.getCurrentItemPosition();

                        if( isLeft ) {
                              if( mStartIndex != itemPosition ) {
                                    mOnPagerScrollListener.onCurrent( mStartIndex, -1.f );
                                    mOnPagerScrollListener.onNext( mNextIndex, 0.f );
                              } else {
                                    mOnPagerScrollListener.onCurrent( mStartIndex, 0.f );
                                    mOnPagerScrollListener.onNext( mNextIndex, 1.f );
                              }
                        } else {
                              if( mStartIndex != itemPosition ) {
                                    mOnPagerScrollListener.onCurrent( mStartIndex, 1.f );
                                    mOnPagerScrollListener.onNext( mNextIndex, 0.f );
                              } else {
                                    mOnPagerScrollListener.onCurrent( mStartIndex, 0.f );
                                    mOnPagerScrollListener.onNext( mNextIndex, -1.f );
                              }
                        }
                        return;
                  }

                  if( mStartIndex == position ) {
                        isLeft = true;
                        mNextIndex = mStartIndex + 1;
                        if( mNextIndex == mItemViewGroup.getItemCount() ) {
                              mNextIndex = 0;
                        }
                        mOnPagerScrollListener.onCurrent( mStartIndex, -positionOffset );
                        mOnPagerScrollListener.onNext( mNextIndex, 1 - positionOffset );
                  } else {
                        isLeft = false;
                        mNextIndex = position;
                        if( mStartIndex == 0 ) {
                              mNextIndex = mItemViewGroup.getItemCount() - 1;
                        }
                        mOnPagerScrollListener.onCurrent( mStartIndex, 1 - positionOffset );
                        mOnPagerScrollListener.onNext( mNextIndex, -positionOffset );
                  }
            }

            if( mCurrentState == ViewPager.SCROLL_STATE_DRAGGING ) {

                  final float endFlag = 0.f;

                  if( positionOffset == endFlag ) {
                        return;
                  }

                  if( mStartIndex == position ) {
                        isLeft = true;
                        mNextIndex = mStartIndex + 1;
                        if( mNextIndex == mItemViewGroup.getItemCount() ) {
                              mNextIndex = 0;
                        }
                        mOnPagerScrollListener.onCurrent( mStartIndex, -positionOffset );
                        mOnPagerScrollListener.onNext( mNextIndex, 1 - positionOffset );
                  } else {
                        isLeft = false;
                        mNextIndex = position;
                        if( mStartIndex == 0 ) {
                              mNextIndex = mItemViewGroup.getItemCount() - 1;
                        }
                        mOnPagerScrollListener.onCurrent( mStartIndex, 1 - positionOffset );
                        mOnPagerScrollListener.onNext( mNextIndex, -positionOffset );
                  }
            }
      }

      @Override
      public void onPageSelected ( int position ) {

            if( mOnPagerScrollListener != null ) {
                  mOnPagerScrollListener.onPageSelected( position );
            }
      }

      @Override
      public void onPageScrollStateChanged ( int state ) {

            mCurrentState = state;
            if( state == ViewPager.SCROLL_STATE_DRAGGING || state == ViewPager.SCROLL_STATE_IDLE ) {
                  mStartIndex = mItemViewGroup.getCurrentItemPosition();
            }
      }
}