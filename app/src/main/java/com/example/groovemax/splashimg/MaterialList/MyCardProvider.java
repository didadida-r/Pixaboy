package com.example.groovemax.splashimg.MaterialList;

import android.view.View;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.example.groovemax.splashimg.R;

/**
 *
 */
public class MyCardProvider extends CardProvider<MyCardProvider> {

    private String title;

    @Override
    public int getLayout() {
        return R.layout.card_layout;
    }

    public MyCardProvider setImageTitle(String title){
        this.title = title;
        notifyDataSetChanged();
        return this;
    }


    @Override
    public void render(View view, Card card) {
        super.render(view, card);

        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(title);

    }

}
