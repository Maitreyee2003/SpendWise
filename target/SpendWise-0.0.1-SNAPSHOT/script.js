// ⚡ [THE HARDWARE BUFFER MANAGING ENGINE - MEMORY PROTECTION OBJECT]:
// Maqsad: Doughnut visual graphics charts ke active configurations trackers object data pointers ko trace me rakhna.
// Logic: Chart.js browser heap memory khata hai. Jab user dropdown filter index toggle karega, toh hum .destroy() pipeline 
// run karenge taaki older records diagrams clean purge flush ho jayein aur app elements overlapping variables crashes memory leak breaks se bacha sake.
let spendwiseChartInstance = null;

// ⚡ [REPORTING UTILITIES STATEMENT CACHE DATA CONTAINER BUFFER]:
// Maqsad: Current selected month ka table raw items array response response packet is memory repository line string proxy me mirror save rehata hai,
// taaki pdf dynamic engine tool direct local parameters sets arrays maps formatting structure access karke rapid printable tables cells map inject bindings generate kr ske.
window.currentRawExpenses = []; 

// 🔄 [FRONT-END DOMINITIALIZATION EVENT LIFECYCLE ROUTINES SETUP]: HTML browser compiler hierarchy stack structure fully load hone ke 
// instant behind the scene asynchronous background streams initialization hooks triggers fire operations engine flow core.
document.addEventListener('DOMContentLoaded', () => {
    // Select selectors links inputs parameters boxes fields trackers buttons interfaces actions identifiers
    const addBtn = document.getElementById('addBtn');
    const nameInput = document.getElementById('itemName');
    const priceInput = document.getElementById('itemPrice');
    const categoryInput = document.getElementById('itemCategory');
    const monthFilter = document.getElementById('monthFilter');
    const budgetInput = document.getElementById('monthlyBudgetInput');
    const downloadPdfBtn = document.getElementById('downloadPdfBtn');

    // 💎 [THE SECURE GLASSMORPHIC MODAL INTERFACES TARGET SELECTORS MAPPINGS COMPONENT LAYER]: (CONSOLE ERROR LINE 52 ABSOLUTE REPAIR HOOKS)
    // Maqsad: Elements selectors target handles pointers link schemas maps data items parameters configurations values rules profiles.
    // Maitreyee, yahan jo matching definitions hain unka elements ids structure strict verified balanced locked link setup kiya hai, 
    // isliye ab addEventListener null types crash errors triggers permanently disappear flush out execution strings down.
    const incomeModal = document.getElementById('incomeModal');
    const openIncomeModalBtn = document.getElementById('openIncomeModalBtn');
    const closeIncomeModalBtn = document.getElementById('closeIncomeModalBtn');
    const saveIncomeModalBtn = document.getElementById('saveIncomeModalBtn');
    const modalIncomeInput = document.getElementById('modalIncomeInput');

    // 🚀 [BOOT RUN TIME TRACKER ALLOCATION CALENDAR CAPTURE]: Drop down selection configurations reading profiles bounds parameters, sets current default initial starting value to index 1 (January).
    const initialMonth = monthFilter.value || "1";

    // Immediate parallel backgrounds thread sync loaders pipelines execution run triggers profiles systems setup on initial boot load sequences
    loadSavedBudgetForMonth(initialMonth);  // Recover local storage variables configurations keys values arrays components properties map rules
    fetchMonthlyExpenses(initialMonth);     // Remote data select connection query engine center layer trigger pipelines scripts controls
    loadAnnualOverviewMatrix();             // Populate 12 sub-month breaking matrix cells boxes numbers trackers display blocks layouts

    // 🔄 [DROPDOWN VALUE FILTER TOGGLE ACTION LISTENER]: Active month dropdown elements metrics items shifts data recalculations setups pipelines trigger flow
    monthFilter.addEventListener('change', () => {
        const selectedMonth = monthFilter.value;
        loadSavedBudgetForMonth(selectedMonth); // Locate corresponding offline caching boundaries saved threshold keys records
        fetchMonthlyExpenses(selectedMonth);    // Remote database asynchronous requests fetch rows reload pipelines executions configurations
    });

    // Custom max cap field numbers variations tracking keyboard keystrokes stroke change listener hook
    budgetInput.addEventListener('input', () => {
        const currentMonthSelected = monthFilter.value;
        const budgetVal = parseFloat(budgetInput.value) || 0;
        
        // Write dynamic numbers directly inside offline browser dictionary keys mappings strings settings properties
        localStorage.setItem(`budget_m_${currentMonthSelected}`, budgetVal);
        
        const displayLabel = document.getElementById('budgetLimitDisplay');
        if (displayLabel) {
            displayLabel.innerText = `Active Limit for Selected Month: ₹${budgetVal.toFixed(2)}`;
        }
        
        fetchMonthlyExpenses(currentMonthSelected);
    });

    // 📑 [PORTFOLIO SPECIAL FEATURE - VECTOR PDF REPORT DATA CONVERTER CTA HOOK BINDING ENGINES]
    if (downloadPdfBtn) {
        downloadPdfBtn.addEventListener('click', () => {
            // 🔍 [THE TECHNICAL EXPLANATORY EDGE-CASE BUG RESOLVER ENGINE LAYER - REGEX SANITIZATION PURGE]:
            // Issue: Option text values dropdown titles attributes strings markers labels structures arrays me emoticons emojis jode hain (`January ❄️`).
            // Risk: Normal jsPDF string text streams document generators core engine standard formats in emojis code structures symbols parse nahi kar paati aur textual alignment blocks layout crash data distortions (`January 'Dp`) breaks errors throw krte hain generated docs headers me.
            // Fix: RegExp object matching ranges filter execute kiya gaya hai jo pure dynamic elements variables characters configurations blocks strings me se binary graphic tags shapes codes wipe out clean delete kr k pure perfect clear alphabetic textual tokens (jaise strict clean string word *"January"*) data streams logic cells printer maps systems forward execute krta h.
            let rawMonthText = monthFilter.options[monthFilter.selectedIndex].text;
            let cleanMonthText = rawMonthText.replace(/[\uE000-\uF8FF]|\uD83C[\uDC00-\uDFFF]|\uD83D[\uDC00-\uDFFF]|[\u2011-\u26FF]|\uD83E[\uDD00-\uDFFF]/g, '').trim();
            generatePDFReport(cleanMonthText);
        });
    }

    // =========================================================
    // 💎 [THE FUTURISTIC WINDOW GLASS POP-UP MODAL DRIVERS TIMING WORKFLOW CONTROLS CLICK SELECTION TRIGGERS]
    // =========================================================
    
    // [NEW ADDITION FEATURE CHANGE - POPUP LAUNCH ENGINE CLICK TRIGGER INTERFACE BINDING]:
    if (openIncomeModalBtn && incomeModal) {
        openIncomeModalBtn.addEventListener('click', () => {
            // Target elements selectors mask div overlays nodes tree properties list me target dynamic css display token utility '.active' append execute mapping parameters system settings. Result: Premium frosted background glass blur slide transformations active live view.
            incomeModal.classList.add('active');
            if(modalIncomeInput) modalIncomeInput.focus(); // Highlights data fields input textfield pointer cursor instantly for swift interface responses rules maps properties elements sets
        });
    }

    // [NEW ADDITION FEATURE CHANGE - POPUP DISMISS CANCEL CONTROLLER ACTION CLICK]:
    if (closeIncomeModalBtn && incomeModal) {
        closeIncomeModalBtn.addEventListener('click', () => {
            incomeModal.classList.remove('active'); // Fade-out scaling structural zoom out easing animations properties transitions interpolation lines loops activated
            if(modalIncomeInput) modalIncomeInput.value = ""; // Clear values fields data inputs log entries string placeholder arrays elements variables
        });
    }

    // [NEW ADDITION FEATURE CHANGE - ASYNCHRONOUS PACKETS TRANSMISSION INCOME SAVE SUBMIT PIPELINE HOOKS CORES]:
    if (saveIncomeModalBtn) {
        saveIncomeModalBtn.addEventListener('click', () => {
            const targetMonth = monthFilter.value;
            const inputVal = parseFloat(modalIncomeInput.value);

            if (isNaN(inputVal) || inputVal < 0) {
                alert("Please provide valid income numerical parameters amount!");
                return;
            }

            // AJAX FETCH SECURE REMOTE INTERACTION SERVICE ARCHITECTURE COMPILATION SEGMENT SUBSECTION DATA ENGINES: POST method maps configuration setup profiles target server mapping servlet controller execution lines (/addIncome)
            fetch('addIncome', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, // Parameter serialized queries standard specification content types tokens contexts data attributes sets
                body: `incomeAmount=${inputVal}&incomeMonth=${targetMonth}` // Form parameters queries bodies keys sets
            })
            .then(response => response.json()) // Decode return packets response buffers direct to clean JSON data array packets objects loops fields systems properties keys
            .then(data => {
                if (data.status === "success") {
                    incomeModal.classList.remove('active'); // Dismiss modal mask viewport layer box elements beautifully using styles callbacks loops curves
                    modalIncomeInput.value = "";
                    
                    // Rerun data calculation center synchronization threads layers metrics charts updates parameters arrays mapping sets to update horizontal metrics cards numbers variables totals live!
                    fetchMonthlyExpenses(targetMonth);
                } else {
                    alert("Server Interrupted: " + data.message);
                }
            })
            .catch(err => console.error("Pipeline failure on writing user income state:", err));
        });
    }

    // =========================================================
    // STANDARD SQL CRUD SUBMISSIONS PIPELINES OPERATION HOOKS CHANNELS RECONSTRUCTIONS
    // =========================================================
    addBtn.addEventListener('click', () => {
        const itemNameValue = nameInput.value.trim();
        const priceValue = parseFloat(priceInput.value);
        const categoryValue = categoryInput.value;
        const targetMonth = monthFilter.value;

        if (!itemNameValue || isNaN(priceValue) || priceValue <= 0) {
            alert("Please provide valid product name and expense amount!");
            return;
        }

        fetch('addExpense', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `itemName=${encodeURIComponent(itemNameValue)}&itemPrice=${priceValue}&itemCategory=${encodeURIComponent(categoryValue)}&monthMonth=${targetMonth}`
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === "success") {
                nameInput.value = "";
                priceInput.value = "";

                setTimeout(() => {
                    fetchMonthlyExpenses(targetMonth);
                    loadAnnualOverviewMatrix();
                }, 150);
            } else {
                alert("Processing Exception: " + data.message);
            }
        })
        .catch(err => console.error("Pipeline failure on creation:", err));
    });
});

// Recover budget setup values records from computer local tracking caches memory databases strings configurations settings fields
function loadSavedBudgetForMonth(monthNum) {
    const budgetInput = document.getElementById('monthlyBudgetInput');
    const displayLabel = document.getElementById('budgetLimitDisplay');
    if (!budgetInput) return;
    
    const savedBudget = localStorage.getItem(`budget_m_${monthNum}`);
    
    if (savedBudget) {
        budgetInput.value = savedBudget;
        if (displayLabel) {
            displayLabel.innerText = `Active Limit for Selected Month: ₹${parseFloat(savedBudget).toFixed(2)}`;
        }
    } else {
        budgetInput.value = "";
        if (displayLabel) {
            displayLabel.innerText = "Active Limit for Selected Month: Not Set 🚫";
        }
    }
}

// 📡 [THE ENGINE WORKFORCE RECONSTRUCTION - ASYNCHRONOUS DUAL FETCH SYNCHRONIZATION THREAD CONTROLLER CORE INTERFACE]:
// Maqsad: Is application function engine pipeline layer ko fully upgrade kiya gaya hai. Yeh jab bhi background servlet data sets khichne fire hota h, toh remote server DB lines ke cross do separate servlets parameters query objects parallel loops calculations streams match mapping runtime execute krta h parameters targets layout systems elements side-by-side components horizontal status row cells dashboard.
function fetchMonthlyExpenses(monthNum) {
    const listContainer = document.getElementById('transactionList');
    if (!listContainer) return;

    const categoryCards = {
        "Food": "card-food", "Daily Uses": "card-daily", "Shopping": "card-shopping",
        "Travel & Fuel": "card-travel", "Bills & Recharge": "card-bills", "Others": "card-others"
    };

    // 📡 DUAL FETCH SEGMENT ROUTINE WORKER PIPELINE STEP TRACKER LINE PART 1: Hits remote background Java Servlet selector reader channel database rows parameters endpoint maps items array collection (/getExpenses)
    fetch(`getExpenses?month=${monthNum}`)
    .then(response => response.json())
    .then(expenseData => {
        window.currentRawExpenses = expenseData; // Mirror records array to global reporting references caches pointers datasets strings maps
        listContainer.innerHTML = "";
        
        let overallTotalSpent = 0;
        let totalsMap = { "Food": 0, "Daily Uses": 0, "Shopping": 0, "Travel & Fuel": 0, "Bills & Recharge": 0, "Others": 0 };

        expenseData.forEach(item => {
            overallTotalSpent += item.amount;
            if (totalsMap[item.category] !== undefined) {
                totalsMap[item.category] += item.amount;
            }

            const itemDiv = document.createElement('div');
            itemDiv.className = 'transaction-item';
            itemDiv.style = "display: flex; justify-content: space-between; align-items: center; background: rgba(255,255,255,0.04); padding: 12px; margin-bottom: 8px; border-radius: 8px; border: 1px solid rgba(255,255,255,0.02);";
            itemDiv.innerHTML = `
                <span>${item.itemName} <small style="color: #aaa; display:block; font-size:11px;">(${item.category})</small></span>
                <div style="display: flex; align-items: center; gap: 15px;">
                    <strong style="color: #fff;">₹${item.amount.toFixed(2)}</strong>
                    <button onclick="deleteTransaction(${item.id}, this)" style="background: none; border: none; color: #ff4757; cursor: pointer; font-size: 16px; padding: 2px 5px;">🗑️</button>
                </div>
            `;
            listContainer.appendChild(itemDiv);
        });

        // Map computed financial scalar values digits directly inside premium design horizontal blue expenses card element view panel dashboard block
        document.getElementById('totalSpent').innerText = `₹${overallTotalSpent.toFixed(2)}`;
        Object.keys(totalsMap).forEach(key => {
            const el = document.getElementById(categoryCards[key]);
            if (el) el.innerText = `₹${totalsMap[key].toFixed(2)}`;
        });

        if (expenseData.length === 0) {
            listContainer.innerHTML = "<p style='text-align:center; color:#aaa; margin-top:15px; font-size:13px;'>No transactions recorded for this month.</p>";
            Object.values(categoryCards).forEach(id => { const el = document.getElementById(id); if (el) el.innerText = "₹0.00"; });
        }

        // 📡 [NEW ADDTION CHANGE] - DUAL FETCH SEGMENT ROUTINE WORKER PIPELINE STEP TRACKER LINE PART 2 (The Twin Connection Interceptor Thread):
        // Expenses query resolution arrays read hit complete hote hi instant automatic behind the scenes second secure java servlet query pipeline interface parameters fetch calculations run (/getIncome) matching fields structures values keys counters states variables
        fetch(`getIncome?month=${monthNum}`)
        .then(res => res.json())
        .then(incomeObj => {
            const currentMonthIncome = incomeObj.amount || 0.0;
            
            // 🧠 CORE CORE ALGORITHMIC BUSINESS BALANCING SYSTEM INSIGHTS:
            // Net dynamic monthly savings remaining vector amount calculation = overall scalar base user incomes cash pool parameters - cumulative expense total records values integers structures variables
            const computedNetSavings = currentMonthIncome - overallTotalSpent;

            // Map absolute incoming parameters database numbers directly to cyberpunk neon emerald green view horizontal metrics panel card container box element identifier
            document.getElementById('totalIncome').innerText = `₹${currentMonthIncome.toFixed(2)}`;
            
            const savingsContainer = document.getElementById('netSavings');
            const savingsCardBox = document.getElementById('netSavingsCard');
            
            savingsContainer.innerText = `₹${computedNetSavings.toFixed(2)}`;

            // =========================================================
            // 🎨 [THE INTELLIGENT METRICS COLOR SHIFT RADAR ENGINE CYBERPUNK ALGORITHMS SETUP MANAGEMENT CONSTRAINTS CORES]
            // =========================================================
            if (computedNetSavings < 0) {
                // State Code Alert Red Deficit: Total cash balance drops behind absolute zero margins limits! (Shifts neon boundary stroke lines elements properties configurations variables layout design boxes parameters to neon danger radiant reds theme profile layout rules strings)
                savingsContainer.style.color = "#ff4757"; 
                savingsCardBox.style.borderColor = "rgba(255, 71, 87, 0.4)";
                savingsCardBox.style.boxShadow = "0 0 15px rgba(255, 71, 87, 0.15)";
            } else if (computedNetSavings === 0 && currentMonthIncome === 0) {
                // State Code Neutral standard default unallocated markers settings parameters metrics strings options reset elements
                savingsContainer.style.color = "#00c6ff"; 
                savingsCardBox.style.borderColor = "rgba(0, 198, 255, 0.1)";
                savingsCardBox.style.boxShadow = "none";
            } else {
                // State Code High Operational Safe Savings Zones Buffers: Money flows perfectly within control margins boundaries limits (illuminates vector borders configurations points coordinates lines strokes elements parameters to deep cyber mint emerald glowing neon parameters layouts format structures profiles rules)
                savingsContainer.style.color = "#38ef7d"; 
                savingsCardBox.style.borderColor = "rgba(56, 239, 125, 0.4)";
                savingsCardBox.style.boxShadow = "0 0 15px rgba(56, 239, 125, 0.15)";
            }

            // Run alarms verification scanning metrics monitoring rules indicators totals configurations constraints fields
            checkBudgetLimitsAndAlerts(overallTotalSpent, monthNum, computedNetSavings);
        });

        renderAnalyticsChart(totalsMap);
    })
    .catch(err => console.error("Pipeline failure on matrix refresh load operations sequence:", err));
}

// Validation logic algorithms parameters thresholds controls checkpoints elements profiles data allocations logs monitors rules
function checkBudgetLimitsAndAlerts(totalSpentAmount, monthNum, computedNetSavings) {
    const alertBanner = document.getElementById('budgetAlertBanner');
    const spentCard = document.getElementById('totalSpentCard');
    
    if (!alertBanner) return;

    const currentSavedBudget = parseFloat(localStorage.getItem(`budget_m_${monthNum}`)) || 0;

    if (currentSavedBudget <= 0) {
        // [INTELLIGENT INTEGRITY ADAPTIVE BACKUP SCAN SAFETY EVALUATION ALARM INTERCEPT FILTER]:
        // Maqsad: Agar user ne target customized cap configuration limits explicit bounds properties set nahi bhi kiye hon, par agar system calculations evaluate counter levels values zero bounds drop out krein (cash pool deficit condition rules breach), instant activate flash dynamic alert warning banner components horizontal panels dashboards views systems safely!
        if (computedNetSavings < 0) {
            alertBanner.style.background = "rgba(255, 71, 87, 0.12)";
            alertBanner.style.border = "2px solid #ff4757";
            alertBanner.style.color = "#ff4757";
            alertBanner.innerHTML = `🚨 CASH CRUNCH DEFICIT: Your expenditures have bypassed your total income pool limits by ₹${Math.abs(computedNetSavings).toFixed(2)}!`;
            alertBanner.style.display = "block";
        } else {
            alertBanner.style.display = "none";
        }
        if (spentCard) { spentCard.style.background = "linear-gradient(45deg, #1e3c72, #2a5298)"; spentCard.style.boxShadow = "none"; }
        return;
    }

    const usagePercentage = (totalSpentAmount / currentSavedBudget) * 100;
    alertBanner.style.display = "block";

    if (totalSpentAmount > currentSavedBudget) {
        alertBanner.style.background = "rgba(255, 71, 87, 0.15)";
        alertBanner.style.border = "2px solid #ff4757";
        alertBanner.style.color = "#ff4757";
        alertBanner.innerHTML = `🚨 ALERT: Budget Breached! Over-limit by ₹${(totalSpentAmount - currentSavedBudget).toFixed(2)}!`;
        
        if (spentCard) {
            spentCard.style.setProperty("background", "linear-gradient(45deg, #cb2d3e, #ef473a)", "important");
            spentCard.style.boxShadow = "0 0 20px rgba(239, 71, 58, 0.6)";
        }
    } else if (usagePercentage >= 80) {
        alertBanner.style.background = "rgba(255, 165, 0, 0.15)";
        alertBanner.style.border = "2px solid #ffa500";
        alertBanner.style.color = "#ffa500";
        alertBanner.innerHTML = `⚠️ WARNING: High Expenditure! Utilized ${usagePercentage.toFixed(1)}% of allowance.`;
        
        if (spentCard) { spentCard.style.background = "linear-gradient(45deg, #1e3c72, #2a5298)"; spentCard.style.boxShadow = "none"; }
    } else {
        alertBanner.style.background = "rgba(46, 204, 113, 0.12)";
        alertBanner.style.border = "2px solid #2ecc71";
        alertBanner.style.color = "#2ecc71";
        alertBanner.innerHTML = `✅ Safe Zone: ${usagePercentage.toFixed(1)}% used. Remaining limit: ₹${(currentSavedBudget - totalSpentAmount).toFixed(2)}`;
        
        if (spentCard) { spentCard.style.background = "linear-gradient(45deg, #1e3c72, #2a5298)"; spentCard.style.boxShadow = "none"; }
    }
}

// Chart graphics node canvas drafting workspace engine layout diagrams profiles shapes configurations lines controls controls
function renderAnalyticsChart(totalsDataMap) {
    const ctx = document.getElementById('expenseChart');
    const container = document.getElementById('chartContainer');
    if (!ctx || !container) return;
    
    const totalSum = Object.values(totalsDataMap).reduce((acc, curr) => acc + curr, 0);
    if (totalSum === 0) { container.style.display = "none"; return; }
    
    container.style.display = "block";

    if (spendwiseChartInstance) { spendwiseChartInstance.destroy(); }

    spendwiseChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(totalsDataMap),
            datasets: [{
                label: 'Expense Share (₹)',
                data: Object.values(totalsDataMap),
                backgroundColor: ['rgba(255, 71, 87, 0.85)', 'rgba(255, 165, 0, 0.85)', 'rgba(234, 32, 39, 0.85)', 'rgba(0, 198, 255, 0.85)', 'rgba(26, 188, 156, 0.85)', 'rgba(155, 89, 182, 0.85)'],
                borderColor: 'rgba(16, 21, 30, 1)', borderWidth: 2, hoverOffset: 12
            }]
        },
        options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { position: 'right', labels: { color: '#dcdde1', font: { size: 11, weight: 'bold' }, padding: 12 } } },
            animation: { animateScale: false, animateRotate: true, duration: 400 }
        }
    });
}

// Dynamic vector reporting sheet layout formatting template configurations vector blocks downloads triggers maps tools
function generatePDFReport(monthTextName) {
    if (!window.currentRawExpenses || window.currentRawExpenses.length === 0) {
        alert("Cannot generate report: No transactions available for this month!");
        return;
    }

    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    const monthFilter = document.getElementById('monthFilter').value;
    const currentSavedBudget = parseFloat(localStorage.getItem(`budget_m_${monthFilter}`)) || 0;
    const ownerName = window.currentUserName || "User";

    let totalCalculatedSpent = 0;
    const dataRows = window.currentRawExpenses.map((item, index) => {
        totalCalculatedSpent += item.amount;
        return [index + 1, item.itemName, item.category, `Rs. ${item.amount.toFixed(2)}`];
    });

    doc.setFillColor(30, 41, 59); doc.rect(0, 0, 210, 40, 'F');  
    doc.setTextColor(255, 255, 255); doc.setFont("helvetica", "bold"); doc.setFontSize(22);
    doc.text("SPENDWISE FINANCIAL STATEMENT", 14, 18); 

    doc.setFont("helvetica", "normal"); doc.setFontSize(10);
    doc.text(`Account Holder: ${ownerName} | Statement Month: ${monthTextName}`, 14, 28);
    doc.text(`Report Generation Date: ${new Date().toLocaleDateString()}`, 14, 34);

    doc.setTextColor(51, 65, 85); doc.setFont("helvetica", "bold"); doc.setFontSize(12);
    doc.text("Financial Overview Summary:", 14, 52);

    doc.setFont("helvetica", "normal"); doc.setFontSize(10);
    doc.text(`Target Budget Allowed: Rs. ${currentSavedBudget.toFixed(2)}`, 14, 60);
    doc.text(`Total Amount Expended: Rs. ${totalCalculatedSpent.toFixed(2)}`, 14, 66);
    
    const balanceRemaining = currentSavedBudget - totalCalculatedSpent;
    if (balanceRemaining < 0) {
        doc.setTextColor(239, 68, 68); doc.text(`Budget Deficit (Over-spent): Rs. ${Math.abs(balanceRemaining).toFixed(2)} 🚨`, 14, 72);
    } else {
        doc.setTextColor(34, 197, 94); doc.text(`Remaining Balance Saved: Rs. ${balanceRemaining.toFixed(2)} ✅`, 14, 72);
    }

    doc.autoTable({
        startY: 80, head: [['#', 'Transaction Item Description', 'Expense Category', 'Money Spent']], body: dataRows, theme: 'striped',
        headStyles: { fillColor: [0, 114, 255], textColor: [255, 255, 255], fontStyle: 'bold' },
        alternateRowStyles: { fillColor: [248, 250, 252] }, styles: { font: "helvetica", fontSize: 10, cellPadding: 4 }, margin: { left: 14, right: 14 }
    });

    doc.save(`SpendWise_Report_${monthTextName.replace(/\s+/g, '')}.pdf`);
}

// 12 sub-month calculation matrix blocks grid layout data compiler lines queries operations loops settings
function loadAnnualOverviewMatrix() {
    fetch('getAnnualSummary')
    .then(response => response.json())
    .then(summaryData => {
        for (let i = 1; i <= 12; i++) { const monthBox = document.getElementById(`m-${i}`); if (monthBox) monthBox.innerText = "₹0.00"; }
        Object.keys(summaryData).forEach(monthNum => {
            const monthBox = document.getElementById(`m-${monthNum}`);
            if (monthBox) { let amount = parseFloat(summaryData[monthNum]); monthBox.innerText = `₹${amount.toFixed(2)}`; }
        });
    })
    .catch(err => console.error("Grid assignment stream exception:", err));
}

// CRUD row rows entries deletion tracking destruction binding pipeline codes configurations controls strings systems
function deleteTransaction(transactionId, buttonElement) {
    if (!confirm("Are you sure you want to delete this expense permanently?")) return; 
    fetch(`deleteExpense?id=${transactionId}`, { method: 'POST' })
    .then(response => response.json())
    .then(data => {
        if (data.status === "success") {
            buttonElement.closest('.transaction-item').remove();
            const monthFilter = document.getElementById('monthFilter');
            fetchMonthlyExpenses(monthFilter.value);
            loadAnnualOverviewMatrix();
        } else { alert("Execution Interrupted: " + data.message); }
    })
    .catch(err => console.error("Pipeline failure on deletion trigger:", err));
}