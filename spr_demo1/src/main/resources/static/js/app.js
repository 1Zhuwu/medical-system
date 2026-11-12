/**
 * 医疗挂号系统 · 前端
 * 纯原生 JS，无框架无构建，直接由 Spring Boot 静态资源提供。
 */
(function () {
  'use strict';

  var el = function (id) { return document.getElementById(id); };
  var $nav = el('nav'), $title = el('pageTitle'), $desc = el('pageDesc'),
      $filters = el('filters'), $keyword = el('keyword'),
      $thead = el('thead'), $tbody = el('tbody'), $pager = el('pager'),
      $mask = el('mask'), $dialogTitle = el('dialogTitle'), $form = el('form'),
      $toast = el('toast'), $conn = el('connState');

  var GENDER = { '1': '男', '2': '女' };

  /* ================= 字典（下拉框数据源） ================= */
  var dict = { departments: [] };

  /* ================= 视图配置 ================= */
  var VIEWS = {
    department: {
      nav: '科室管理',
      title: '科室管理',
      desc: '维护科室编码、名称、类型与联系方式',
      search: '按科室编码或名称搜索',
      api: '/api/departments',
      columns: [
        { key: 'deptCode', label: '科室编码' },
        { key: 'deptName', label: '科室名称' },
        { key: 'deptType', label: '类型' },
        { key: 'location', label: '位置' },
        { key: 'phone', label: '联系电话' },
        { key: 'sortNo', label: '排序', cls: 'num' },
        { key: 'status', label: '状态', render: function (v) {
            return v === 1 ? '<span class="tag on">启用</span>' : '<span class="tag off">停用</span>';
          } }
      ],
      filters: [
        { name: 'status', label: '全部状态', options: [{ v: '1', t: '启用' }, { v: '0', t: '停用' }] }
      ],
      fields: [
        { name: 'deptCode', label: '科室编码', required: true, placeholder: '如 NK' },
        { name: 'deptName', label: '科室名称', required: true, placeholder: '如 内科' },
        { name: 'deptType', label: '科室类型', type: 'select', options: [
            { v: '门诊', t: '门诊' }, { v: '住院', t: '住院' }, { v: '医技', t: '医技' }, { v: '急诊', t: '急诊' }
          ] },
        { name: 'location', label: '科室位置', placeholder: '如 门诊楼3层' },
        { name: 'phone', label: '联系电话', placeholder: '如 0571-88001001' },
        { name: 'sortNo', label: '排序号', type: 'number', placeholder: '数字越小越靠前' },
        { name: 'status', label: '状态', type: 'select', options: [{ v: '1', t: '启用' }, { v: '0', t: '停用' }] },
        { name: 'description', label: '科室简介', type: 'textarea', placeholder: '选填' }
      ]
    },

    doctor: {
      nav: '医生管理',
      title: '医生管理',
      desc: '维护医生工号、职称、所属科室与出诊状态',
      search: '按工号、姓名或手机号搜索',
      api: '/api/doctors',
      columns: [
        { key: 'doctorNo', label: '工号' },
        { key: 'doctorName', label: '姓名' },
        { key: 'gender', label: '性别', render: function (v) { return GENDER[v] || '—'; } },
        { key: 'deptName', label: '所属科室' },
        { key: 'title', label: '职称' },
        { key: 'phone', label: '手机号' },
        { key: 'regFee', label: '挂号费', cls: 'num', render: function (v) { return '¥' + Number(v).toFixed(2); } },
        { key: 'status', label: '状态', render: function (v) {
            return v === 1 ? '<span class="tag on">出诊</span>' : '<span class="tag off">停诊</span>';
          } }
      ],
      filters: [
        { name: 'deptId', label: '全部科室', source: 'departments' },
        { name: 'title', label: '全部职称', options: [
            { v: '主任医师', t: '主任医师' }, { v: '副主任医师', t: '副主任医师' },
            { v: '主治医师', t: '主治医师' }, { v: '住院医师', t: '住院医师' }
          ] },
        { name: 'status', label: '全部状态', options: [{ v: '1', t: '出诊' }, { v: '0', t: '停诊' }] }
      ],
      fields: [
        { name: 'doctorNo', label: '医生工号', required: true, placeholder: '如 D1005' },
        { name: 'doctorName', label: '姓名', required: true },
        { name: 'gender', label: '性别', type: 'select', options: [{ v: '1', t: '男' }, { v: '2', t: '女' }] },
        { name: 'deptId', label: '所属科室', type: 'select', source: 'departments', required: true },
        { name: 'phone', label: '手机号', required: true, placeholder: '11 位手机号' },
        { name: 'idCard', label: '身份证号', required: true, placeholder: '18 位，末位可为 X' },
        { name: 'title', label: '职称', type: 'select', options: [
            { v: '主任医师', t: '主任医师' }, { v: '副主任医师', t: '副主任医师' },
            { v: '主治医师', t: '主治医师' }, { v: '住院医师', t: '住院医师' }
          ] },
        { name: 'regFee', label: '挂号费', type: 'number', placeholder: '如 20' },
        { name: 'specialty', label: '擅长领域', placeholder: '如 高血压、冠心病' },
        { name: 'status', label: '出诊状态', type: 'select', options: [{ v: '1', t: '出诊' }, { v: '0', t: '停诊' }] },
        { name: 'introduction', label: '个人简介', type: 'textarea', placeholder: '选填' }
      ]
    },

    patient: {
      nav: '患者管理',
      title: '患者管理',
      desc: '维护患者档案，列表中的手机号与身份证已脱敏',
      search: '按档案号、姓名或手机号搜索',
      api: '/api/patients',
      columns: [
        { key: 'patientNo', label: '档案号' },
        { key: 'patientName', label: '姓名' },
        { key: 'gender', label: '性别', render: function (v) { return GENDER[v] || '—'; } },
        { key: 'phone', label: '手机号' },
        { key: 'idCard', label: '身份证号' },
        { key: 'birthDate', label: '出生日期' },
        { key: 'address', label: '住址' }
      ],
      filters: [
        { name: 'gender', label: '全部性别', options: [{ v: '1', t: '男' }, { v: '2', t: '女' }] }
      ],
      fields: [
        { name: 'patientNo', label: '患者档案号', required: true, placeholder: '如 P20260003' },
        { name: 'patientName', label: '姓名', required: true },
        { name: 'gender', label: '性别', type: 'select', options: [{ v: '1', t: '男' }, { v: '2', t: '女' }] },
        { name: 'birthDate', label: '出生日期', type: 'date' },
        { name: 'phone', label: '手机号', required: true, placeholder: '11 位手机号' },
        { name: 'idCard', label: '身份证号', required: true, placeholder: '18 位，末位可为 X' },
        { name: 'address', label: '家庭住址', placeholder: '选填' },
        { name: 'emergencyContact', label: '紧急联系人', placeholder: '选填' },
        { name: 'emergencyPhone', label: '紧急联系电话', placeholder: '选填' }
      ]
    }
  };

  /* ================= 状态 ================= */
  var state = {
    view: 'department',
    pageNum: 1,
    pageSize: 10,
    keyword: '',
    filters: {},
    editingId: null
  };

  /* ================= 请求封装 ================= */
  function api(url, options) {
    options = options || {};
    if (options.body) {
      options.headers = { 'Content-Type': 'application/json' };
      options.body = JSON.stringify(options.body);
    }
    return fetch(url, options).then(function (res) {
      return res.text().then(function (text) {
        var body;
        try {
          body = JSON.parse(text);
        } catch (e) {
          throw new Error('服务器返回了非预期内容（HTTP ' + res.status + '）');
        }
        if (body.code !== 200) {
          throw new Error(body.message || '请求失败（HTTP ' + res.status + '）');
        }
        return body;
      });
    }, function () {
      throw new Error('无法连接后端服务，请确认应用已启动');
    });
  }

  /* ================= 工具 ================= */
  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  var toastTimer;
  function toast(msg, type) {
    $toast.textContent = msg;
    $toast.className = 'toast show' + (type ? ' ' + type : '');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(function () { $toast.className = 'toast'; }, 2800);
  }

  function optionsOf(f) {
    if (f.options) return f.options;
    if (f.source === 'departments') {
      return dict.departments.map(function (d) { return { v: d.id, t: d.deptName }; });
    }
    return [];
  }

  /* ================= 渲染：导航 ================= */
  function applyViewMeta() {
    var v = VIEWS[state.view];
    $title.textContent = v.title;
    $desc.textContent = v.desc;
    $keyword.placeholder = v.search || '搜索';
  }

  function renderNav() {
    $nav.innerHTML = Object.keys(VIEWS).map(function (key) {
      return '<a class="nav-item' + (key === state.view ? ' active' : '') + '" data-view="' + key + '">'
        + VIEWS[key].nav + '</a>';
    }).join('');
  }

  /* ================= 渲染：筛选栏 ================= */
  function renderFilters() {
    var v = VIEWS[state.view];
    $filters.innerHTML = (v.filters || []).map(function (f) {
      var opts = optionsOf(f).map(function (o) {
        return '<option value="' + escapeHtml(o.v) + '">' + escapeHtml(o.t) + '</option>';
      }).join('');
      return '<select class="input filter" data-name="' + f.name + '">'
        + '<option value="">' + escapeHtml(f.label) + '</option>' + opts + '</select>';
    }).join('');
  }

  /* ================= 渲染：表格 ================= */
  function renderTable(page) {
    var v = VIEWS[state.view];
    var cols = v.columns;

    $thead.innerHTML = '<tr>' + cols.map(function (c) {
      return '<th>' + c.label + '</th>';
    }).join('') + '<th>操作</th></tr>';

    if (!page.records.length) {
      $tbody.innerHTML = '<tr><td class="loading" colspan="' + (cols.length + 1) + '">暂无数据</td></tr>';
      return;
    }

    $tbody.innerHTML = page.records.map(function (row) {
      return '<tr>' + cols.map(function (c) {
        var val = row[c.key];
        var html;
        if (c.render) {
          html = c.render(val, row);
        } else if (val === null || val === undefined || val === '') {
          html = '<span class="empty">—</span>';
        } else {
          html = escapeHtml(val);
        }
        return '<td' + (c.cls ? ' class="' + c.cls + '"' : '') + '>' + html + '</td>';
      }).join('')
        + '<td>'
        + '<button class="link-btn" data-act="edit" data-id="' + row.id + '">编辑</button>'
        + '<button class="link-btn danger" data-act="del" data-id="' + row.id + '" data-name="'
        + escapeHtml(row.deptName || row.doctorName || row.patientName || '') + '">删除</button>'
        + '</td></tr>';
    }).join('');
  }

  /* ================= 渲染：分页 ================= */
  function pageNumbers(cur, total) {
    if (total <= 7) {
      var arr = [];
      for (var i = 1; i <= total; i++) arr.push(i);
      return arr;
    }
    var out = [1];
    var start = Math.max(2, cur - 2), end = Math.min(total - 1, cur + 2);
    if (start > 2) out.push('...');
    for (var j = start; j <= end; j++) out.push(j);
    if (end < total - 1) out.push('...');
    out.push(total);
    return out;
  }

  function renderPager(page) {
    var total = page.pages || 1;
    var nums = pageNumbers(page.current, total).map(function (n) {
      if (n === '...') return '<span class="pg-dots">…</span>';
      return '<button class="pg' + (n === page.current ? ' active' : '') + '" data-page="' + n + '">' + n + '</button>';
    }).join('');

    $pager.innerHTML =
      '<span class="pager-info">共 ' + page.total + ' 条 · 第 ' + page.current + ' / ' + total + ' 页</span>'
      + '<div class="pager-btns">'
      + '<button class="pg" data-page="' + (page.current - 1) + '"' + (page.current <= 1 ? ' disabled' : '') + '>上一页</button>'
      + nums
      + '<button class="pg" data-page="' + (page.current + 1) + '"' + (page.current >= total ? ' disabled' : '') + '>下一页</button>'
      + '</div>';
  }

  /* ================= 加载列表 ================= */
  function loadList() {
    var v = VIEWS[state.view];
    var params = new URLSearchParams();
    params.set('pageNum', state.pageNum);
    params.set('pageSize', state.pageSize);
    if (state.keyword) params.set('keyword', state.keyword);
    Object.keys(state.filters).forEach(function (k) {
      if (state.filters[k] !== '') params.set(k, state.filters[k]);
    });

    $tbody.innerHTML = '<tr><td class="loading" colspan="' + (v.columns.length + 1) + '">加载中…</td></tr>';
    $pager.innerHTML = '';

    api(v.api + '?' + params.toString()).then(function (res) {
      renderTable(res.data);
      renderPager(res.data);
    }, function (err) {
      $tbody.innerHTML = '<tr><td class="loading" colspan="' + (v.columns.length + 1) + '">'
        + escapeHtml(err.message) + '</td></tr>';
    });
  }

  /* ================= 弹窗 ================= */
  function renderField(f) {
    var html = '<div class="field' + (f.type === 'textarea' ? ' full' : '') + '">';
    html += '<label>' + f.label + (f.required ? '<i>*</i>' : '') + '</label>';

    if (f.type === 'select') {
      html += '<select class="input" name="' + f.name + '"><option value="">请选择</option>'
        + optionsOf(f).map(function (o) {
            return '<option value="' + escapeHtml(o.v) + '">' + escapeHtml(o.t) + '</option>';
          }).join('')
        + '</select>';
    } else if (f.type === 'textarea') {
      html += '<textarea class="input" name="' + f.name + '" rows="3" placeholder="'
        + (f.placeholder || '') + '"></textarea>';
    } else {
      html += '<input class="input" type="' + (f.type || 'text') + '" name="' + f.name
        + '" placeholder="' + (f.placeholder || '') + '">';
    }

    return html + '</div>';
  }

  function openDialog(id) {
    var v = VIEWS[state.view];
    state.editingId = id == null ? null : id;

    var show = function (row) {
      $dialogTitle.textContent = (row ? '编辑' : '新增') + v.nav.replace('管理', '');
      $form.innerHTML = v.fields.map(renderField).join('');
      if (row) {
        v.fields.forEach(function (f) {
          var input = $form.querySelector('[name="' + f.name + '"]');
          var val = row[f.name];
          if (input && val !== null && val !== undefined) input.value = val;
        });
      }
      $mask.classList.add('show');
      var first = $form.querySelector('.input');
      if (first) first.focus();
    };

    if (id == null) {
      show(null);
      return;
    }

    // 编辑：先拉详情。列表里的手机号、身份证是脱敏值，不能直接用来回填表单
    api(v.api + '/' + id).then(function (res) {
      show(res.data);
    }, function (err) {
      toast(err.message, 'error');
    });
  }

  function closeDialog() {
    $mask.classList.remove('show');
    state.editingId = null;
  }

  /* ================= 提交 ================= */
  function collect() {
    var v = VIEWS[state.view];
    var data = {};

    for (var i = 0; i < v.fields.length; i++) {
      var f = v.fields[i];
      var input = $form.querySelector('[name="' + f.name + '"]');
      var val = (input.value || '').trim();

      if (f.required && !val) {
        toast('请填写「' + f.label + '」', 'error');
        input.focus();
        return null;
      }

      if (val === '') {
        // 数字、日期、下拉为空时不传，避免后端类型转换失败；文本字段传空串以支持清空
        if (f.type === 'number' || f.type === 'date' || f.type === 'select') continue;
        data[f.name] = '';
        continue;
      }

      data[f.name] = f.type === 'number' ? Number(val) : val;
    }
    return data;
  }

  function submit() {
    var v = VIEWS[state.view];
    var data = collect();
    if (data === null) return;

    var isEdit = state.editingId != null;
    var url = isEdit ? v.api + '/' + state.editingId : v.api;

    $form.querySelectorAll('.input').forEach(function (i) { i.disabled = true; });

    api(url, { method: isEdit ? 'PUT' : 'POST', body: data }).then(function () {
      toast(isEdit ? '修改成功' : '新增成功');
      closeDialog();
      if (!isEdit) state.pageNum = 1;
      loadList();
    }, function (err) {
      toast(err.message, 'error');
    }).then(function () {
      $form.querySelectorAll('.input').forEach(function (i) { i.disabled = false; });
    });
  }

  function remove(id, name) {
    var label = name ? '「' + name + '」' : '这条记录';
    if (!window.confirm('确定删除' + label + '吗？')) return;

    api(VIEWS[state.view].api + '/' + id, { method: 'DELETE' }).then(function () {
      toast('删除成功');
      loadList();
    }, function (err) {
      toast(err.message, 'error');
    });
  }

  /* ================= 切换视图 ================= */
  function switchView(key) {
    if (state.view === key) return;
    state.view = key;
    state.pageNum = 1;
    state.keyword = '';
    state.filters = {};

    $keyword.value = '';

    applyViewMeta();
    renderNav();
    renderFilters();
    loadList();
  }

  /* ================= 事件绑定 ================= */
  $nav.addEventListener('click', function (e) {
    var item = e.target.closest('.nav-item');
    if (item) switchView(item.dataset.view);
  });

  $tbody.addEventListener('click', function (e) {
    var btn = e.target.closest('button[data-act]');
    if (!btn) return;
    var id = btn.dataset.id;
    if (btn.dataset.act === 'edit') openDialog(id);
    else remove(id, btn.dataset.name);
  });

  $pager.addEventListener('click', function (e) {
    var btn = e.target.closest('button[data-page]');
    if (!btn || btn.disabled) return;
    state.pageNum = Number(btn.dataset.page);
    loadList();
  });

  $filters.addEventListener('change', function (e) {
    var sel = e.target.closest('select[data-name]');
    if (!sel) return;
    state.filters[sel.dataset.name] = sel.value;
    state.pageNum = 1;
    loadList();
  });

  el('btnSearch').addEventListener('click', function () {
    state.keyword = $keyword.value.trim();
    state.pageNum = 1;
    loadList();
  });

  $keyword.addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
      state.keyword = $keyword.value.trim();
      state.pageNum = 1;
      loadList();
    }
  });

  el('btnReset').addEventListener('click', function () {
    state.keyword = '';
    state.filters = {};
    state.pageNum = 1;
    $keyword.value = '';
    renderFilters();
    loadList();
  });

  el('btnCreate').addEventListener('click', function () { openDialog(null); });

  el('btnSubmit').addEventListener('click', submit);

  $form.addEventListener('submit', function (e) {
    e.preventDefault();
    submit();
  });

  el('btnClose').addEventListener('click', closeDialog);
  el('btnCancel').addEventListener('click', closeDialog);
  $mask.addEventListener('click', function (e) {
    if (e.target === $mask) closeDialog();
  });
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && $mask.classList.contains('show')) closeDialog();
  });

  /* ================= 启动 ================= */
  function checkConnection() {
    fetch('/hello').then(function (res) {
      return res.text();
    }).then(function () {
      $conn.textContent = '后端已连接';
      $conn.className = 'conn ok';
    }, function () {
      $conn.textContent = '后端未连接';
      $conn.className = 'conn bad';
    });
  }

  function init() {
    checkConnection();
    renderNav();
    applyViewMeta();

    // 医生表单与筛选需要科室下拉，先取一次数据
    api('/api/departments?pageSize=200').then(function (res) {
      dict.departments = res.data.records || [];
    }, function () {
      dict.departments = [];
    }).then(function () {
      renderFilters();
      loadList();
    });
  }

  init();
})();
