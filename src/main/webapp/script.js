let spendwiseChartInstance = null;
window.currentRawExpenses = [];

// ======= TOAST NOTIFICATION SYSTEM =======
function showToast(message, type = 'success') {
    const existing = document.querySelector('.sw-toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'sw-toast';

    const colors = {
        success: { bg: 'rgba(56,239,125,0.1)', border: '#38ef7d', color: '#38ef7d', icon: '✅' },
        error: { bg: 'rgba(255,71,87,0.1)', border: '#ff4757', color: '#ff4757', icon: '❌' },
        warning: { bg: 'rgba(255,165,0,0.1)', border: '#ffa502', color: '#ffa502', icon: '⚠️' },
        info: { bg: 'rgba(0,198,255,0.1)', border: '#00c6ff', color: '#00c6ff', icon: 'ℹ️' }
    };

    const c = colors[type] || colors.success;

    toast.innerHTML = `
        <span style="font-size:18px;">${c.icon}</span>
        <span style="font-size:13px; font-weight:600; color:${c.color};">${message}</span>
        <button onclick="this.parentElement.remove()" 
                style="background:none; border:none; color:${c.color}; 
                       cursor:pointer; font-size:16px; margin-left:8px;">×</button>
    `;

    Object.assign(toast.style, {
        position: 'fixed',
        top: '20px',
        right: '20px',
        display: 'flex',
        alignItems: 'center',
        gap: '10px',
        padding: '14px 18px',
        background: c.bg,
        border: `1px solid ${c.border}`,
        borderRadius: '12px',
        backdropFilter: 'blur(20px)',
        zIndex: '9999',
        boxShadow: `0 8px 32px rgba(0,0,0,0.4), 0 0 0 1px rgba(255,255,255,0.05)`,
        animation: 'toastIn 0.4s cubic-bezier(0.175,0.885,0.32,1.275) forwards',
        maxWidth: '320px',
        fontFamily: 'Inter, sans-serif'
    });

    if (!document.getElementById('toast-style')) {
        const style = document.createElement('style');
        style.id = 'toast-style';
        style.textContent = `
            @keyframes toastIn {
                from { opacity:0; transform:translateX(100px) scale(0.8); }
                to { opacity:1; transform:translateX(0) scale(1); }
            }
            @keyframes toastOut {
                from { opacity:1; transform:translateX(0) scale(1); }
                to { opacity:0; transform:translateX(100px) scale(0.8); }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'toastOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// ======= CONFETTI ANIMATION =======
function launchConfetti() {
    const colors = ['#00c6ff','#0072ff','#38ef7d','#ff4757','#ffa502','#7c5cbf','#eccc68'];
    const container = document.createElement('div');
    container.style.cssText = `
        position:fixed; top:0; left:0; width:100%; height:100%;
        pointer-events:none; z-index:9998; overflow:hidden;
    `;
    document.body.appendChild(container);

    if (!document.getElementById('confetti-style')) {
        const style = document.createElement('style');
        style.id = 'confetti-style';
        style.textContent = `
            @keyframes confettiFall {
                0% { transform: translateY(-20px) rotate(0deg); opacity:1; }
                100% { transform: translateY(100vh) rotate(720deg); opacity:0; }
            }
            @keyframes confettiSway {
                0%,100% { margin-left: 0px; }
                25% { margin-left: 30px; }
                75% { margin-left: -30px; }
            }
        `;
        document.head.appendChild(style);
    }

    for (let i = 0; i < 60; i++) {
        const piece = document.createElement('div');
        const color = colors[Math.floor(Math.random() * colors.length)];
        const size = Math.random() * 8 + 4;
        const isCircle = Math.random() > 0.5;

        Object.assign(piece.style, {
            position: 'absolute',
            left: Math.random() * 100 + '%',
            top: '-20px',
            width: size + 'px',
            height: size + 'px',
            background: color,
            borderRadius: isCircle ? '50%' : '2px',
            animation: `confettiFall ${Math.random() * 2 + 1.5}s ease-in ${Math.random() * 0.5}s forwards,
                        confettiSway ${Math.random() * 1 + 1}s ease-in-out ${Math.random() * 0.5}s infinite`,
            opacity: '1',
            boxShadow: `0 0 6px ${color}80`
        });

        container.appendChild(piece);
    }

    setTimeout(() => container.remove(), 3500);
}

// ======= SUCCESS OVERLAY (when expense added) =======
function showAddSuccessAnimation(itemName, amount, category) {
    const overlay = document.createElement('div');
    overlay.style.cssText = `
        position:fixed; inset:0;
        display:flex; align-items:center; justify-content:center;
        z-index:9997; pointer-events:none;
    `;

    overlay.innerHTML = `
        <div id="successCard" style="
            background: rgba(13,17,23,0.95);
            border: 1px solid rgba(56,239,125,0.3);
            border-radius: 20px;
            padding: 32px 40px;
            text-align: center;
            box-shadow: 0 0 60px rgba(56,239,125,0.15), 0 24px 48px rgba(0,0,0,0.6);
            animation: popIn 0.5s cubic-bezier(0.175,0.885,0.32,1.275) forwards;
            font-family: Inter, sans-serif;
        ">
            <div style="font-size:48px; margin-bottom:12px; animation: bounce 0.6s ease 0.3s both;">✅</div>
            <div style="font-size:22px; font-weight:800; color:#38ef7d; margin-bottom:6px;">
                Transaction Added!
            </div>
            <div style="font-size:14px; color:rgba(255,255,255,0.6); margin-bottom:4px;">
                ${itemName}
            </div>
            <div style="font-size:28px; font-weight:900; color:#00c6ff; letter-spacing:-0.5px;">
                ₹${parseFloat(amount).toFixed(2)}
            </div>
            <div style="
                display:inline-block;
                margin-top:10px;
                padding:4px 12px;
                background:rgba(0,198,255,0.1);
                border:1px solid rgba(0,198,255,0.2);
                border-radius:20px;
                font-size:12px;
                color:#00c6ff;
                font-weight:600;
            ">${category}</div>
        </div>
    `;

    if (!document.getElementById('popup-style')) {
        const style = document.createElement('style');
        style.id = 'popup-style';
        style.textContent = `
            @keyframes popIn {
                from { opacity:0; transform:scale(0.5) translateY(30px); }
                to { opacity:1; transform:scale(1) translateY(0); }
            }
            @keyframes popOut {
                from { opacity:1; transform:scale(1); }
                to { opacity:0; transform:scale(0.8) translateY(-20px); }
            }
            @keyframes bounce {
                0%,100% { transform:scale(1); }
                50% { transform:scale(1.3); }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(overlay);

    setTimeout(() => {
        const card = document.getElementById('successCard');
        if (card) {
            card.style.animation = 'popOut 0.4s ease forwards';
        }
        setTimeout(() => overlay.remove(), 400);
    }, 2000);
}

// ======= CATEGORY CARD PULSE ANIMATION =======
function pulseCategoryCard(category) {
    const categoryMap = {
        "Food": "card-food",
        "Daily Uses": "card-daily",
        "Shopping": "card-shopping",
        "Travel & Fuel": "card-travel",
        "Bills & Recharge": "card-bills",
        "Others": "card-others"
    };

    const cardId = categoryMap[category];
    if (!cardId) return;

    const el = document.getElementById(cardId);
    if (!el) return;

    const parent = el.closest('.cat-card');
    if (!parent) return;

    if (!document.getElementById('pulse-style')) {
        const style = document.createElement('style');
        style.id = 'pulse-style';
        style.textContent = `
            @keyframes cardPulse {
                0% { transform:scale(1); }
                30% { transform:scale(1.08); }
                60% { transform:scale(0.96); }
                100% { transform:scale(1); }
            }
            .cat-pulse {
                animation: cardPulse 0.6s cubic-bezier(0.175,0.885,0.32,1.275) !important;
            }
        `;
        document.head.appendChild(style);
    }

    parent.classList.add('cat-pulse');
    setTimeout(() => parent.classList.remove('cat-pulse'), 600);
}

// ======= BUTTON LOADING STATE =======
function setButtonLoading(btn, loading) {
    if (loading) {
        btn.dataset.original = btn.innerHTML;
        btn.innerHTML = `
            <span style="display:inline-flex; align-items:center; gap:8px;">
                <span style="
                    display:inline-block;
                    width:14px; height:14px;
                    border:2px solid rgba(255,255,255,0.3);
                    border-top-color:white;
                    border-radius:50%;
                    animation:spin 0.7s linear infinite;
                "></span>
                Adding...
            </span>
        `;
        btn.disabled = true;
        btn.style.opacity = '0.8';

        if (!document.getElementById('spin-style')) {
            const s = document.createElement('style');
            s.id = 'spin-style';
            s.textContent = `@keyframes spin { to { transform:rotate(360deg); } }`;
            document.head.appendChild(s);
        }
    } else {
        btn.innerHTML = btn.dataset.original || '➕ Add Transaction';
        btn.disabled = false;
        btn.style.opacity = '1';
    }
}

// ======= STAT CARD VALUE ANIMATION =======
function animateValue(elementId, newValue) {
    const el = document.getElementById(elementId);
    if (!el) return;

    el.style.transform = 'scale(1.15)';
    el.style.transition = 'transform 0.3s ease';

    setTimeout(() => {
        el.innerText = newValue;
        el.style.transform = 'scale(1)';
    }, 150);
}

// ======= MAIN INIT =======
document.addEventListener('DOMContentLoaded', () => {
    const addBtn = document.getElementById('addBtn');
    const nameInput = document.getElementById('itemName');
    const priceInput = document.getElementById('itemPrice');
    const categoryInput = document.getElementById('itemCategory');
    const monthFilter = document.getElementById('monthFilter');
    const budgetInput = document.getElementById('monthlyBudgetInput');

    const incomeModal = document.getElementById('incomeModal');
    const openIncomeModalBtn = document.getElementById('openIncomeModalBtn');
    const closeIncomeModalBtn = document.getElementById('closeIncomeModalBtn');
    const saveIncomeModalBtn = document.getElementById('saveIncomeModalBtn');
    const modalIncomeInput = document.getElementById('modalIncomeInput');

    const initialMonth = monthFilter.value || "1";
    loadSavedBudgetForMonth(initialMonth);
    fetchMonthlyExpenses(initialMonth);
    loadAnnualOverviewMatrix();

    // Month filter change
    monthFilter.addEventListener('change', () => {
        const selectedMonth = monthFilter.value;
        loadSavedBudgetForMonth(selectedMonth);
        fetchMonthlyExpenses(selectedMonth);

        // Update download report link
        const monthNames = {
            1:'January',2:'February',3:'March',4:'April',
            5:'May',6:'June',7:'July',8:'August',
            9:'September',10:'October',11:'November',12:'December'
        };
        const dlBtn = document.getElementById('downloadReportBtn');
        if (dlBtn) {
            dlBtn.href = `downloadReport?month=${monthNames[parseInt(selectedMonth)]}`;
        }
    });

    // Budget input
    budgetInput.addEventListener('input', () => {
        const currentMonth = monthFilter.value;
        const budgetVal = parseFloat(budgetInput.value) || 0;
        localStorage.setItem(`budget_m_${currentMonth}`, budgetVal);
        const display = document.getElementById('budgetLimitDisplay');
        if (display) display.innerText = `Active Limit: ₹${budgetVal.toFixed(2)}`;
        fetchMonthlyExpenses(currentMonth);
    });

    // Income modal open
    if (openIncomeModalBtn && incomeModal) {
        openIncomeModalBtn.addEventListener('click', () => {
            incomeModal.classList.add('active');
            if (modalIncomeInput) modalIncomeInput.focus();
        });
    }

    // Income modal close
    if (closeIncomeModalBtn && incomeModal) {
        closeIncomeModalBtn.addEventListener('click', () => {
            incomeModal.classList.remove('active');
            if (modalIncomeInput) modalIncomeInput.value = '';
        });
    }

    // Close modal on outside click
    if (incomeModal) {
        incomeModal.addEventListener('click', (e) => {
            if (e.target === incomeModal) {
                incomeModal.classList.remove('active');
            }
        });
    }

    // Save income
    if (saveIncomeModalBtn) {
        saveIncomeModalBtn.addEventListener('click', () => {
            const targetMonth = monthFilter.value;
            const inputVal = parseFloat(modalIncomeInput.value);

            if (isNaN(inputVal) || inputVal < 0) {
                showToast('Please enter a valid income amount!', 'error');
                return;
            }

            fetch('addIncome', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `incomeAmount=${inputVal}&incomeMonth=${targetMonth}`
            })
            .then(r => r.json())
            .then(data => {
                if (data.status === 'success') {
                    incomeModal.classList.remove('active');
                    modalIncomeInput.value = '';
                    fetchMonthlyExpenses(targetMonth);
                    showToast(`Income updated: ₹${inputVal.toLocaleString()}`, 'success');
                } else {
                    showToast('Failed to update income!', 'error');
                }
            })
            .catch(() => showToast('Network error!', 'error'));
        });
    }

    // ======= ADD EXPENSE WITH ANIMATIONS =======
    addBtn.addEventListener('click', () => {
        const itemNameValue = nameInput.value.trim();
        const priceValue = parseFloat(priceInput.value);
        const categoryValue = categoryInput.value;
        const targetMonth = monthFilter.value;

        if (!itemNameValue || isNaN(priceValue) || priceValue <= 0) {
            showToast('Please enter item name and valid amount!', 'error');

            // Shake animation on empty inputs
            [nameInput, priceInput].forEach(input => {
                if (!input.value.trim() || (input === priceInput && (isNaN(priceValue) || priceValue <= 0))) {
                    input.style.animation = 'shake 0.4s ease';
                    input.style.borderColor = '#ff4757';
                    setTimeout(() => {
                        input.style.animation = '';
                        input.style.borderColor = '';
                    }, 500);
                }
            });

            if (!document.getElementById('shake-style')) {
                const s = document.createElement('style');
                s.id = 'shake-style';
                s.textContent = `
                    @keyframes shake {
                        0%,100%{transform:translateX(0)}
                        20%{transform:translateX(-8px)}
                        40%{transform:translateX(8px)}
                        60%{transform:translateX(-6px)}
                        80%{transform:translateX(6px)}
                    }
                `;
                document.head.appendChild(s);
            }
            return;
        }

        // Loading state
        setButtonLoading(addBtn, true);

        fetch('addExpense', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `itemName=${encodeURIComponent(itemNameValue)}&itemPrice=${priceValue}&itemCategory=${encodeURIComponent(categoryValue)}&monthMonth=${targetMonth}`
        })
        .then(r => r.json())
        .then(data => {
            setButtonLoading(addBtn, false);

            if (data.status === 'success') {
                nameInput.value = '';
                priceInput.value = '';

                // 🎉 All animations fire together!
                launchConfetti();
                showAddSuccessAnimation(itemNameValue, priceValue, categoryValue);
                showToast(`Added ₹${priceValue} - ${itemNameValue}`, 'success');

                setTimeout(() => {
                    fetchMonthlyExpenses(targetMonth);
                    loadAnnualOverviewMatrix();
                    pulseCategoryCard(categoryValue);
                }, 300);

            } else {
                showToast('Failed to add expense!', 'error');
            }
        })
        .catch(() => {
            setButtonLoading(addBtn, false);
            showToast('Network error! Check connection.', 'error');
        });
    });

    // Enter key support
    nameInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') priceInput.focus();
    });
    priceInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') addBtn.click();
    });
});

// ======= LOAD SAVED BUDGET =======
function loadSavedBudgetForMonth(monthNum) {
    const budgetInput = document.getElementById('monthlyBudgetInput');
    const displayLabel = document.getElementById('budgetLimitDisplay');
    if (!budgetInput) return;

    const saved = localStorage.getItem(`budget_m_${monthNum}`);
    if (saved) {
        budgetInput.value = saved;
        if (displayLabel) displayLabel.innerText = `Active Limit: ₹${parseFloat(saved).toFixed(2)}`;
    } else {
        budgetInput.value = '';
        if (displayLabel) displayLabel.innerText = 'Active Limit: Not Set';
    }
}

// ======= FETCH MONTHLY EXPENSES =======
function fetchMonthlyExpenses(monthNum) {
    const listContainer = document.getElementById('transactionList');
    if (!listContainer) return;

    const categoryCards = {
        "Food": "card-food",
        "Daily Uses": "card-daily",
        "Shopping": "card-shopping",
        "Travel & Fuel": "card-travel",
        "Bills & Recharge": "card-bills",
        "Others": "card-others"
    };

    fetch(`getExpenses?month=${monthNum}`)
    .then(r => r.json())
    .then(expenseData => {
        window.currentRawExpenses = expenseData;
        listContainer.innerHTML = '';

        let totalSpent = 0;
        let totalsMap = {
            "Food": 0, "Daily Uses": 0, "Shopping": 0,
            "Travel & Fuel": 0, "Bills & Recharge": 0, "Others": 0
        };

        if (expenseData.length === 0) {
            listContainer.innerHTML = `
                <div class="empty-msg">
                    <div style="font-size:32px; margin-bottom:10px;">📭</div>
                    <div>No transactions for this month</div>
                </div>
            `;
            Object.values(categoryCards).forEach(id => {
                const el = document.getElementById(id);
                if (el) el.innerText = '₹0.00';
            });
        } else {
            expenseData.forEach(item => {
                totalSpent += item.amount;
                if (totalsMap[item.category] !== undefined) {
                    totalsMap[item.category] += item.amount;
                }

                const div = document.createElement('div');
                div.className = 'transaction-item fade-in-item';
                div.innerHTML = `
                    <div class="txn-left">
                        <div class="txn-name">${item.itemName}</div>
                        <div class="txn-cat">(${item.category})</div>
                    </div>
                    <div class="txn-right">
                        <span class="txn-amount">₹${item.amount.toFixed(2)}</span>
                        <button class="txn-del"
                                onclick="deleteTransaction(${item.id}, this)"
                                title="Delete">🗑️</button>
                    </div>
                `;
                listContainer.appendChild(div);
            });

            Object.keys(totalsMap).forEach(key => {
                const el = document.getElementById(categoryCards[key]);
                if (el) el.innerText = `₹${totalsMap[key].toFixed(2)}`;
            });
        }

        animateValue('totalSpent', `₹${totalSpent.toFixed(2)}`);

        fetch(`getIncome?month=${monthNum}`)
        .then(r => r.json())
        .then(incomeObj => {
            const income = incomeObj.amount || 0;
            const savings = income - totalSpent;

            animateValue('totalIncome', `₹${income.toFixed(2)}`);
            animateValue('netSavings', `₹${savings.toFixed(2)}`);

            const savingsEl = document.getElementById('netSavings');
            if (savingsEl) {
                savingsEl.style.color = savings < 0 ? '#ff4757' : '#00c6ff';
            }

            checkBudgetAlerts(totalSpent, monthNum, savings);
        });

        renderChart(totalsMap);
    })
    .catch(err => console.error('Fetch error:', err));
}

// ======= BUDGET ALERTS =======
function checkBudgetAlerts(totalSpent, monthNum, savings) {
    const banner = document.getElementById('budgetAlertBanner');
    const progressBar = document.getElementById('budgetProgressBar');
    if (!banner) return;

    const budget = parseFloat(localStorage.getItem(`budget_m_${monthNum}`)) || 0;

    if (budget <= 0) {
        if (savings < 0) {
            banner.style.cssText = `
                display:block;
                background:rgba(255,71,87,0.1);
                border:1px solid #ff4757;
                color:#ff4757;
                border-radius:10px;
                padding:10px 14px;
                font-size:12px;
                font-weight:600;
                text-align:center;
                margin-top:8px;
            `;
            banner.innerHTML = `🚨 Cash deficit! Over income by ₹${Math.abs(savings).toFixed(2)}`;
        } else {
            banner.style.display = 'none';
        }
        if (progressBar) progressBar.style.width = '0%';
        return;
    }

    const pct = (totalSpent / budget) * 100;
    banner.style.display = 'block';
    banner.style.borderRadius = '10px';
    banner.style.padding = '10px 14px';
    banner.style.fontSize = '12px';
    banner.style.fontWeight = '600';
    banner.style.textAlign = 'center';
    banner.style.marginTop = '8px';

    if (progressBar) {
        progressBar.style.width = Math.min(pct, 100) + '%';
        if (pct >= 100) {
            progressBar.style.background = 'linear-gradient(90deg, #ff4757, #ff6b81)';
        } else if (pct >= 80) {
            progressBar.style.background = 'linear-gradient(90deg, #ffa502, #ffdd59)';
        } else {
            progressBar.style.background = 'linear-gradient(90deg, #38ef7d, #00c6ff)';
        }
    }

    if (totalSpent > budget) {
        banner.style.background = 'rgba(255,71,87,0.1)';
        banner.style.border = '1px solid #ff4757';
        banner.style.color = '#ff4757';
        banner.innerHTML = `🚨 Budget exceeded by ₹${(totalSpent - budget).toFixed(2)}!`;
    } else if (pct >= 80) {
        banner.style.background = 'rgba(255,165,0,0.1)';
        banner.style.border = '1px solid #ffa502';
        banner.style.color = '#ffa502';
        banner.innerHTML = `⚠️ ${pct.toFixed(1)}% used — ₹${(budget - totalSpent).toFixed(2)} remaining`;
    } else {
        banner.style.background = 'rgba(56,239,125,0.08)';
        banner.style.border = '1px solid rgba(56,239,125,0.3)';
        banner.style.color = '#38ef7d';
        banner.innerHTML = `✅ Safe Zone — ${pct.toFixed(1)}% used, ₹${(budget - totalSpent).toFixed(2)} left`;
    }
}

// ======= CHART =======
function renderChart(totalsMap) {
    const ctx = document.getElementById('expenseChart');
    const container = document.getElementById('chartContainer');
    if (!ctx || !container) return;

    const total = Object.values(totalsMap).reduce((a, b) => a + b, 0);
    if (total === 0) { container.style.display = 'none'; return; }

    container.style.display = 'block';
    if (spendwiseChartInstance) spendwiseChartInstance.destroy();

    spendwiseChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(totalsMap),
            datasets: [{
                data: Object.values(totalsMap),
                backgroundColor: [
                    'rgba(255,71,87,0.85)',
                    'rgba(30,144,255,0.85)',
                    'rgba(236,204,104,0.85)',
                    'rgba(46,213,115,0.85)',
                    'rgba(255,165,2,0.85)',
                    'rgba(124,92,191,0.85)'
                ],
                borderColor: '#0d1117',
                borderWidth: 2,
                hoverOffset: 14
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right',
                    labels: {
                        color: '#8b949e',
                        font: { size: 11, weight: '600', family: 'Inter' },
                        padding: 14,
                        usePointStyle: true,
                        pointStyleWidth: 8
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(13,17,23,0.95)',
                    borderColor: 'rgba(255,255,255,0.1)',
                    borderWidth: 1,
                    titleColor: '#f0f6fc',
                    bodyColor: '#8b949e',
                    padding: 12,
                    callbacks: {
                        label: (ctx) => ` ₹${ctx.raw.toFixed(2)}`
                    }
                }
            },
            animation: { animateRotate: true, duration: 600 }
        }
    });
}

// ======= ANNUAL GRID =======
function loadAnnualOverviewMatrix() {
    fetch('getAnnualSummary')
    .then(r => r.json())
    .then(data => {
        for (let i = 1; i <= 12; i++) {
            const el = document.getElementById(`m-${i}`);
            if (el) el.innerText = '₹0';
        }
        Object.keys(data).forEach(m => {
            const el = document.getElementById(`m-${m}`);
            if (el) el.innerText = `₹${parseFloat(data[m]).toFixed(0)}`;
        });
    })
    .catch(err => console.error('Annual grid error:', err));
}

// ======= DELETE TRANSACTION =======
function deleteTransaction(id, btn) {
    if (!confirm('Delete this expense permanently?')) return;

    const item = btn.closest('.transaction-item');
    if (item) {
        item.style.transition = 'all 0.3s ease';
        item.style.opacity = '0';
        item.style.transform = 'translateX(100%)';
    }

    setTimeout(() => {
        fetch(`deleteExpense?id=${id}`, { method: 'POST' })
        .then(r => r.json())
        .then(data => {
            if (data.status === 'success') {
                const monthFilter = document.getElementById('monthFilter');
                fetchMonthlyExpenses(monthFilter.value);
                loadAnnualOverviewMatrix();
                showToast('Transaction deleted!', 'info');
            } else {
                showToast('Delete failed!', 'error');
                if (item) { item.style.opacity = '1'; item.style.transform = ''; }
            }
        })
        .catch(() => showToast('Network error!', 'error'));
    }, 300);
}