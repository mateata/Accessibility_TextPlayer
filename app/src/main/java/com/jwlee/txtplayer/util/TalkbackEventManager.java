package com.jwlee.txtplayer.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityEvent;
import android.view.ViewGroup;
import android.util.Log;

public class TalkbackEventManager {
	
	private final String Tag = "TalkbackEventManager";
	
	private Context	mContext;
	
	private OnInitializeAccessibilityEventListener	mOnInitializeAccessibilityEventListener = null;
	private OnDispatchPopulateAccessibilityEventListener mOnDispatchPopulateAccessibilityEventListener= null;
	private OnPopulateAccessibilityEventListener mOnPopulateAccessibilityEventListener = null;
	private OnViewClickedListener mOnViewClickedListener= null;
	private OnViewFocusedListener mOnViewFocusedListener= null;
	private OnViewSelectedListener mOnViewSelectedListener= null;
	private OnViewScrolledListener mOnViewScrolledListener= null;
	private OnViewHoverEnterListener mOnViewHoverEnterListener= null;
	private OnViewHoverExitListener mOnViewHoverExitListener= null;
	private OnViewLongClickedListener mOnViewLongClickedListener= null;
	private OnViewTextChangedListener mOnViewTextChangedListener= null;
	private OnViewTextSelectionChangedListener mOnViewTextSelectionChangedListener= null;
	private OnViewAccessibilityFocusedListener mOnViewAccessibilityFocusedListener= null;
	private OnViewAccessibilityFocusClearedListener mOnViewAccessibilityFocusClearedListener= null;

	
	public TalkbackEventManager(Context context) {
		mContext = context;
	}
	
	public void setMainView(View view) {
		if( view == null )
			return;
		
		if( view instanceof ViewGroup ) {
			int i;
			ViewGroup viewGroup = (ViewGroup)view;
			
			for( i = 0 ; i < viewGroup.getChildCount() ; i++ ) {
				View subView = viewGroup.getChildAt(i);
				setMainView(subView);
			}
		} else if( view.getId() > 0 ){
			view.setAccessibilityDelegate(mAccessibiltiyDelegate);
		}
	}
	
	public void setResourceIDs(int[] resourceIDs) {
		Activity activity = (Activity)mContext;
		if( activity == null )
			return;
		
		for( int i = 0 ; i < resourceIDs.length ; i++ ) {
			
			 View view = (View)activity.findViewById(resourceIDs[i]);
			 
			 if( view != null )
				 view.setAccessibilityDelegate(mAccessibiltiyDelegate);
		}
	}
	
	public void setResourceID(int resourceID) {
		Activity activity = (Activity)mContext;
		if( activity == null )
			return;
		
		 View view = (View)activity.findViewById(resourceID);
		 
		 if( view != null )
			 view.setAccessibilityDelegate(mAccessibiltiyDelegate);
	}
	
	public void setView(View view) {
		
		 if( view != null )
			 view.setAccessibilityDelegate(mAccessibiltiyDelegate);
	}
	
	public void setOnInitializeAccessibilityEventListener(OnInitializeAccessibilityEventListener listener) {
		mOnInitializeAccessibilityEventListener = listener;
	}
	
	public void setOnDispatchPopulateAccessibilityEventListener(OnDispatchPopulateAccessibilityEventListener listener) {
		mOnDispatchPopulateAccessibilityEventListener = listener;
	}
	
	public void setOnPopulateAccessibilityEventListener(OnPopulateAccessibilityEventListener listener) {
		mOnPopulateAccessibilityEventListener = listener;
	}
	
	public void setOnViewClickedListener(OnViewClickedListener listener) {
		mOnViewClickedListener = listener;
	}
	
	public void setOnViewFocusedListener(OnViewFocusedListener listener) {
		mOnViewFocusedListener = listener;
	}
	
	public void setOnViewSelectedListener(OnViewSelectedListener listener) {
		mOnViewSelectedListener = listener;
	}
	
	public void setOnViewScrolledListener(OnViewScrolledListener listener) {
		mOnViewScrolledListener = listener;
	}
	
	public void setOnViewHoverEnterListener(OnViewHoverEnterListener listener) {
		mOnViewHoverEnterListener = listener;
	}
	
	public void setOnViewHoverExitListener(OnViewHoverExitListener listener) {
		mOnViewHoverExitListener = listener;
	}
	
	public void setOnViewLongClickedListener(OnViewLongClickedListener listener) {
		mOnViewLongClickedListener = listener;
	}
	
	public void setOnViewTextChangedListener(OnViewTextChangedListener listener) {
		mOnViewTextChangedListener = listener;
	}
	
	public void setOnViewTextSelectionChangedListener(OnViewTextSelectionChangedListener listener) {
		mOnViewTextSelectionChangedListener = listener;
	}
	
	public void setOnViewAccessibilityFocusedListener(OnViewAccessibilityFocusedListener listener) {
		mOnViewAccessibilityFocusedListener = listener;
	}
	
	public void setOnViewAccessibilityFocusClearedListener(OnViewAccessibilityFocusClearedListener listener) {
		mOnViewAccessibilityFocusClearedListener = listener;
	}
	
	private AccessibilityDelegate	mAccessibiltiyDelegate = new AccessibilityDelegate(){

		@Override
		public void  onInitializeAccessibilityEvent(View host, final AccessibilityEvent event) {
			if( mOnInitializeAccessibilityEventListener == null  || 
				mOnInitializeAccessibilityEventListener.onInitializeAccessibilityEvent(host, event) == true ) {
				super.onInitializeAccessibilityEvent(host, event);
			} 
			event.setScrollable(true);
			event.setEnabled(true);
		
		}

		@Override 
		public boolean  dispatchPopulateAccessibilityEvent (View host, AccessibilityEvent event) {
			
			boolean bRet = true;
			if( mOnDispatchPopulateAccessibilityEventListener != null ) {
				bRet = mOnDispatchPopulateAccessibilityEventListener.dispatchPopulateAccessibilityEvent(host, event);
			}
			
			if( bRet ) {
				switch(event.getEventType()) {
				case AccessibilityEvent.TYPE_VIEW_CLICKED:
					if( mOnViewClickedListener != null )
						mOnViewClickedListener.onViewClicked(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_FOCUSED:
					if( mOnViewFocusedListener != null )
						mOnViewFocusedListener.onViewFocused(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_SELECTED:
					if( mOnViewSelectedListener != null )
						mOnViewSelectedListener.onViewSelected(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_SCROLLED:
					if( mOnViewScrolledListener != null )
						mOnViewScrolledListener.onViewScrolled(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
					if( mOnViewHoverEnterListener != null )
						mOnViewHoverEnterListener.onViewHoverEnter(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
					if( mOnViewHoverExitListener != null )
						mOnViewHoverExitListener.onViewHoverExit(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
					if( mOnViewLongClickedListener != null )
						mOnViewLongClickedListener.onViewLongClicked(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
					if( mOnViewTextChangedListener != null )
						mOnViewTextChangedListener.onViewTextChanged(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
					if( mOnViewTextSelectionChangedListener != null )
						mOnViewTextSelectionChangedListener.onViewTextSelectionChanged(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
					if( mOnViewAccessibilityFocusedListener != null )
						mOnViewAccessibilityFocusedListener.onViewAccessibilityFocused(host);
					break;
				case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
					if( mOnViewAccessibilityFocusClearedListener != null )
						mOnViewAccessibilityFocusClearedListener.onViewAccessibilityFocusCleared(host);
					break;
				}
			}
			
			return bRet;
		}
	
		@Override
		public void  onPopulateAccessibilityEvent (View host, AccessibilityEvent event)  {
			if( mOnPopulateAccessibilityEventListener == null ||
				mOnPopulateAccessibilityEventListener.onPopulateAccessibilityEvent(host, event) ) { 
				super.onPopulateAccessibilityEvent(host, event);
			}
		}
		
		@Override
		public boolean  onRequestSendAccessibilityEvent (ViewGroup host, View child, AccessibilityEvent event) {
			return true;
		}

	};
	
	public interface OnInitializeAccessibilityEventListener {
		public boolean onInitializeAccessibilityEvent(View host, AccessibilityEvent event);
	}
	
	public interface OnDispatchPopulateAccessibilityEventListener {
        public boolean dispatchPopulateAccessibilityEvent (View host, AccessibilityEvent event);
	}
	
	public interface OnPopulateAccessibilityEventListener {
		public boolean onPopulateAccessibilityEvent(View host, AccessibilityEvent event);
	}

    public interface OnViewClickedListener {
        public void onViewClicked(View view);
    }

    public interface OnViewFocusedListener {
        public void onViewFocused(View view);
    }

    public interface OnViewSelectedListener {
        public void onViewSelected(View view);
    }

    public interface OnViewScrolledListener {
        public void onViewScrolled(View view);
    }

    public interface OnViewHoverEnterListener {
        public void onViewHoverEnter(View view);
    }

    public interface OnViewHoverExitListener {
        public void onViewHoverExit(View view);
    }

    public interface OnViewLongClickedListener {
        public void onViewLongClicked(View view);
    }

    public interface OnViewTextChangedListener {
        public void onViewTextChanged(View view);
    }

    public interface OnViewTextSelectionChangedListener {
        public void onViewTextSelectionChanged(View view);
    }

    public interface OnViewAccessibilityFocusedListener {
        public void onViewAccessibilityFocused(View view);
    }
        
    public interface OnViewAccessibilityFocusClearedListener {
        public void onViewAccessibilityFocusCleared(View view);
    }
}
