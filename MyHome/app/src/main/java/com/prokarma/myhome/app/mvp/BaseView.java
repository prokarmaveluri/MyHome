
package com.prokarma.myhome.app.mvp;

@Deprecated
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
