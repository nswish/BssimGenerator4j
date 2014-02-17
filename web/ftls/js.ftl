// 页面初始化
efform_onload = function() {

};

// 查询按钮事件
button_query_onclick = function()
{
    efgrid.submitInqu("ef_grid_result", "${firstModule?lower_case}", "${jsName}", "query"); // Grid的查询提交
};

// 新增按钮事件
button_create_onclick = function()
{
    EFColorbox({href:"#dialog", title:"新增", width:"50%", inline:true}); // 打开模态对话框

    $('#colorbox').appendTo('#mainForm'); // 将模态对话框节点移动到表单节点内

    $('#dialog *[name|=save-0]').val(''); // 清空模态对话框
};

// 复制新增按钮事件
button_copy_onclick = function()
{
    var grid = efform.getGrid("ef_grid_result");

    if(grid.getCheckedRowCount() == 0){
        alert("请选择一条记录后，再做操作！");
        return;
    }

    EFColorbox({href:"#dialog", title:"复制", width:"50%", inline:true}); // 打开模态对话框

    $('#colorbox').appendTo('#mainForm'); // 将模态对话框节点移动到表单节点内

    // 将选中的第一行数据复制到对话框中
    var row = grid.getRowData(grid.getCheckedRows()[0]);
    row['id'] = undefined; // 去除ID
    $('#dialog *[name|=save-0]').each(function(){
        var key = this.name.replace('save-0-', '');
        $(this).val($.trim(row[key]));
    });
}

// 编辑按钮事件
button_edit_onclick = function()
{
    var grid = efform.getGrid("ef_grid_result");

    if(grid.getCheckedRowCount() == 0){
        alert("请选择一条记录后，再做操作！");
        return;
    }

    EFColorbox({href:"#dialog", title:"编辑", width:"50%", inline:true}); // 打开模态对话框

    $('#colorbox').appendTo('#mainForm'); // 将模态对话框节点移动到表单节点内

    // 将选中的第一行数据复制到对话框中
    var row = grid.getRowData(grid.getCheckedRows()[0]);
    $('#dialog *[name|=save-0]').each(function(){
        var key = this.name.replace('save-0-', '');
        $(this).val($.trim(row[key]));
    });
};

// 删除按钮事件
button_delete_onclick = function()
{
    if(efform.getGrid("ef_grid_result").getCheckedRowCount() == 0){
        alert("请选择一条记录后，再做操作！");
        return;
    }

    efgrid.submitForm( "ef_grid_result", "${firstModule?lower_case}", "${jsName}", "delete", true); // Grid的表单提交
};

// 保存按钮事件
button_save_onclick = function()
{
    efgrid.submitForm( "ef_grid_result", "${firstModule?lower_case}", "${jsName}", "save", true); // Grid的表单提交
};