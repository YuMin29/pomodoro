package com.yumin.pomodoro.ui.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public class UnPeekLiveData<T> extends ProtectedUnPeekLiveData<T> {
    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    @Deprecated
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        throw new IllegalArgumentException("请不要在 UnPeekLiveData 中使用 observe 方法。" +
                "取而代之的是在 Activity 和 Fragment 中分别使用 observeInActivity 和 observeInFragment 来观察。\n\n" +
                "Taking into account the normal permission of preventing backflow logic, " +
                " do not use observeForever to communicate between pages." +
                "Instead, you can use ObserveInActivity and ObserveInFragment methods " +
                "to observe in Activity and Fragment respectively.");
    }

    @Override
    @Deprecated
    public void observeForever(@NonNull Observer<? super T> observer) {
        throw new IllegalArgumentException("出于生命周期安全的考虑，请不要在 UnPeekLiveData 中使用 observeForever 方法。\n\n" +
                "Considering avoid lifecycle security issues," +
                " do not use observeForever for communication between pages.");
    }

    public static class Builder<T> {
        private boolean mIsAllowNullValue;

        public Builder<T> setAllowNullValue(boolean allowNullValue) {
            mIsAllowNullValue = allowNullValue;
            return this;
        }

        public UnPeekLiveData<T> create() {
            UnPeekLiveData<T> liveData = new UnPeekLiveData<>();
            liveData.mIsAllowNullValue = mIsAllowNullValue;
            return liveData;
        }
    }
}
