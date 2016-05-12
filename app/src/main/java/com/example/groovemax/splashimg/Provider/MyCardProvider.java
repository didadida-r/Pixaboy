package com.example.groovemax.splashimg.Provider;

import android.view.View;
import android.widget.LinearLayout;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.example.groovemax.splashimg.R;

/**
 *
 */
public class MyCardProvider extends CardProvider<MyCardProvider> {

    private int Drawable;

    @Override
    public int getLayout() {
        return R.layout.card_layout;
    }

    public MyCardProvider setShadowImage(int drawable){
        this.Drawable = drawable;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public void render(View view, Card card) {
        super.render(view, card);

        LinearLayout shadowImage = (LinearLayout) view.findViewById(R.id.shadowImage);
        shadowImage.setBackgroundColor(Drawable);

    }

}
