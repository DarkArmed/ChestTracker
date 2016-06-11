package com.darkarmed.chesttrackerforclashroyale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuiderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuiderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuiderFragment extends Fragment {
    private static final String TAG = "GuiderFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER = "user";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUser;
    private String mParam2;

    private Context mContext;
    private Menu mMenu;
    private View mView;
    private GridView mGridView;
    private ChestAdapter mAdapter;
    private List<Chest> mChests;

    private TextView mMatchedTextView;
    private TextView mMatchedPosTextView;
    private Button mApplyButton;
    private boolean mFuzzy;

    private OnFragmentInteractionListener mListener;

    public enum ChestButtonEnum {
        SILVER(R.id.silver_chest_button, Chest.Type.SILVER),
        GOLDEN(R.id.golden_chest_button, Chest.Type.GOLDEN),
        GIANT(R.id.giant_chest_button, Chest.Type.GIANT),
        MAGICAL(R.id.magical_chest_button, Chest.Type.MAGICAL);
//        SUPER_MAGICAL(R.id.super_magical_chest_button, Chest.Type.SUPER_MAGICAL);

        private int mViewResId;
        private Chest.Type mType;

        ChestButtonEnum(int viewResId, Chest.Type type) {
            mViewResId = viewResId;
            mType = type;
        }

        public int getViewResId() {
            return mViewResId;
        }

        public Chest.Type getType() {
            return mType;
        }
    }

    public GuiderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GuiderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GuiderFragment newInstance(String user, String param2) {
        GuiderFragment fragment = new GuiderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUser = getArguments().getString(ARG_USER);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_guider, container, false);
        mGridView = (GridView) mView.findViewById(R.id.guiderview);
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadChests();
        mAdapter = new ChestAdapter(mContext, mChests, false);
        mGridView.setAdapter(mAdapter);
        mGridView.smoothScrollToPosition(mGridView.getCount() - 1);

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.remove(position);
                checkMatched();
                return true;
            }
        });

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/Supercell-Magic_5.ttf");

        mMatchedTextView = (TextView) mView.findViewById(R.id.matched_text_view);
        mMatchedTextView.setText(getString(R.string.matched_position));
        mMatchedTextView.setTypeface(tf);

        mMatchedPosTextView = (TextView) mView.findViewById(R.id.matched_position_view);
        mMatchedPosTextView.setText("None");
        mMatchedPosTextView.setTypeface(tf);

        mApplyButton = (Button) mView.findViewById(R.id.matched_apply_button);
        mApplyButton.setTypeface(tf);

        checkMatched();

        for (ChestButtonEnum e : ChestButtonEnum.values()) {
            final ImageButton imageButton = (ImageButton) mView.findViewById(e.getViewResId());
            imageButton.setTag(e.getType());
            imageButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            imageButton.setScaleX(0.9f);
                            imageButton.setScaleY(0.9f);
                            break;
                        case MotionEvent.ACTION_UP:
                            imageButton.setScaleX(1f);
                            imageButton.setScaleY(1f);
                            break;
                        default:
                    }
                    return false;
                }
            });

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addChest((Chest.Type) v.getTag());
                    Toast.makeText(mContext, getString(R.string.long_press_to_cancel),
                            Toast.LENGTH_SHORT).show();
                    if (mChests.size() > 4) {
                        checkMatched();
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveChests();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onMatchPositionApply(int pos, int length);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        inflater.inflate(R.menu.menu_guider, menu);
        menu.findItem(R.id.action_fuzzy).setTitle(mFuzzy ?
                R.string.action_fuzzy_off : R.string.action_fuzzy_on);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d(TAG, "TrackerFragment action settings clicked.");
                return true;
            case R.id.action_fuzzy:
                Log.d(TAG, "TrackerFragment action fuzzy clicked.");
                toggleFuzzyMatch();
                return true;
            case R.id.action_clear:
                Log.d(TAG, "TrackerFragment action clear clicked.");
                clearAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private boolean loadChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = chestPref.getString("CHESTS", "");

        if (json.equalsIgnoreCase("")) {
            mChests = new ArrayList<>();
            mFuzzy = false;
            return false;
        } else {
            Log.d(TAG, json);
            mChests = new Gson().fromJson(json, new TypeToken<List<Chest>>() {}.getType());
            mFuzzy = chestPref.getBoolean("FUZZY_MATCH", false);
            return true;
        }
    }

    private boolean saveChests() {
        SharedPreferences chestPref = getActivity().getSharedPreferences(mUser, Context.MODE_PRIVATE);
        String json = new Gson().toJson(mAdapter.getItems());
        chestPref.edit().putString("CHESTS", json).putBoolean("FUZZY_MATCH", mFuzzy).commit();

        Log.d(TAG, json);
        Log.d(TAG, chestPref.getString("CHESTS", ""));

        return true;
    }

    private void addChest(Chest.Type type) {
        if (mAdapter != null){
            mAdapter.add(new Chest(mAdapter.getCount() + 1, type, Chest.Status.OPENED));
            mGridView.smoothScrollToPosition(mGridView.getCount() - 1);
        }
    }

    private void checkMatched() {
        final String chests = getChestSequence();

        ChestMatcher matcher = new ChestMatcher(getString(R.string.chest_loop));

        SortedSet<Map.Entry<Integer, Integer>> matched = matcher.getMatchedPositions(chests, mFuzzy);

        String matchedPosition = "";
        if (matched.size() == 1) {
            matchedPosition = matched.first().getKey().toString();
            mApplyButton.setEnabled(true);

            final Integer finalMatchedPosition = matched.first().getKey();
            final Integer finalMatchedLength = matched.first().getValue();
            mApplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onMatchPositionApply(finalMatchedPosition, finalMatchedLength);
                }
            });

        } else {
            if (matched.size() > 0) {
                int count = 0;
                for (Map.Entry<Integer, Integer> e : matched) {
                    if (e.getValue() < 5 || count > 8) {
                        matchedPosition += "...";
                        break;
                    }
                    count++;
                    matchedPosition += e.getKey().toString() + " ";
                }
            }
            mApplyButton.setEnabled(false);
        }

        mMatchedPosTextView.setText(matchedPosition);
    }

    private String getChestSequence() {
        String chests = "";
        for (Chest chest : mChests) {
            switch (chest.getType()) {
                case SILVER:
                    chests += "s";
                    break;
                case GOLDEN:
                    chests += "g";
                    break;
                case GIANT:
                    chests += "G";
                    break;
                case MAGICAL:
                    chests += "m";
                    break;
                default:
                    chests += "s";
            }
        }
        return chests;
    }

    private void toggleFuzzyMatch() {
        MenuItem indexMenuItem = mMenu.findItem(R.id.action_fuzzy);
        if (mFuzzy) {
            mFuzzy = false;
            indexMenuItem.setTitle(R.string.action_fuzzy_on);
        } else {
            mFuzzy = true;
            indexMenuItem.setTitle(R.string.action_fuzzy_off);
        }
        checkMatched();
    }

    private void clearAll() {
        mAdapter.clear();
        checkMatched();
    }
}
