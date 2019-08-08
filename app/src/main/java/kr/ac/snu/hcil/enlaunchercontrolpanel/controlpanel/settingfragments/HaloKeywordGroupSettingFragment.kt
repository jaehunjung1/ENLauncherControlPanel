package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.DragStartHelper
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.snu.hcil.datahalo.ui.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupDetailsLookup
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupItemTouchHelperCallback
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupRecyclerAdapter
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.OnStartDragListener

class HaloKeywordGroupSettingFragment: androidx.fragment.app.Fragment(), OnStartDragListener{

    private lateinit var appConfigViewModel: AppHaloConfigViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: KeywordGroupRecyclerAdapter
    private lateinit var keywordGroupItemTouchHelper: ItemTouchHelper
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return (inflater.inflate(R.layout.fragment_halo_keyword_group_setting, container, false) as LinearLayout).also{ view ->
            recyclerViewAdapter = KeywordGroupRecyclerAdapter(appConfigViewModel)
            recyclerView = view.findViewById<RecyclerView>(R.id.keyword_group_recycler_view).apply{
                adapter = recyclerViewAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            keywordGroupItemTouchHelper = ItemTouchHelper(KeywordGroupItemTouchHelperCallback(recyclerViewAdapter))
            keywordGroupItemTouchHelper.attachToRecyclerView(recyclerView)



            tracker = SelectionTracker.Builder<Long>(
                    "keywordGroupSelection",
                    recyclerView,
                    StableIdKeyProvider(recyclerView),
                    KeywordGroupDetailsLookup(recyclerView),
                    StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                    SelectionPredicates.createSelectSingleAnything()
            ).build()

            tracker?.addObserver(object: SelectionTracker.SelectionObserver<Long>(){})

            recyclerViewAdapter.tracker = tracker


        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        keywordGroupItemTouchHelper.startDrag(viewHolder)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        tracker?.onRestoreInstanceState(savedInstanceState)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }
}