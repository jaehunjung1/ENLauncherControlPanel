package kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.settingfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.DragStartHelper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel
import kr.ac.snu.hcil.datahalo.visconfig.AppHaloConfig
import kr.ac.snu.hcil.enlaunchercontrolpanel.R
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupDetailsLookup
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupItemTouchHelperCallback
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.KeywordGroupRecyclerAdapter
import kr.ac.snu.hcil.enlaunchercontrolpanel.controlpanel.components.keywordgroup.OnStartDragListener

class HaloKeywordGroupSettingFragment: androidx.fragment.app.Fragment(), OnStartDragListener{

    private lateinit var appConfigViewModel: kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: KeywordGroupRecyclerAdapter
    private lateinit var keywordGroupItemTouchHelper: ItemTouchHelper
    private lateinit var keywordGroupAddButton: MaterialButton

    private var tracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appConfigViewModel = activity?.run{
            ViewModelProviders.of(this).get(kr.ac.snu.hcil.datahalo.viewmodel.AppHaloConfigViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        /*
        val appConfigObserver = Observer<AppHaloConfig>{ newConfig ->
            newConfig?.let{
                recyclerViewAdapter
            }
        }
        appConfigViewModel.appHaloConfigLiveData.observe(this, appConfigObserver)*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return (inflater.inflate(R.layout.fragment_halo_keyword_group_setting, container, false) as LinearLayout).also{ view ->
            recyclerViewAdapter = KeywordGroupRecyclerAdapter(appConfigViewModel).also{
                it.startDragListener = this
            }
            recyclerView = view.findViewById<RecyclerView>(R.id.keyword_group_recycler_view).apply{
                adapter = recyclerViewAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                addItemDecoration(
                        DividerItemDecoration(this.context, RecyclerView.VERTICAL)
                )
            }

            keywordGroupItemTouchHelper = ItemTouchHelper(KeywordGroupItemTouchHelperCallback(recyclerViewAdapter))
            keywordGroupItemTouchHelper.attachToRecyclerView(recyclerView)
            keywordGroupAddButton = view.findViewById<MaterialButton>(R.id.group_add_button).apply{
                setOnClickListener {
                    val context = this.context
                    val addDialog = MaterialAlertDialogBuilder(context).let{ builder ->
                        val editText = EditText(context)
                        builder.setView(editText)
                        builder.setPositiveButton("OK"){_, _ ->
                            editText.text.toString().trim().let{
                                if(it.isNotBlank()){
                                    recyclerViewAdapter.addKeywordGroup(it)
                                }
                            }
                        }
                        builder.setNegativeButton("Cancel"){_, _ ->}
                        builder.create()
                    }
                    addDialog.show()

                }
            }
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