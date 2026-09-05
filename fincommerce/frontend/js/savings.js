/* FinCommerce Savings Module Logic */

let userGoals = [];
let walletBalance = 0;
let savingsTransactions = [];

document.addEventListener('DOMContentLoaded', () => {
  loadSavingsData();
  
  // Set default target date to 3 months in future
  const d = new Date();
  d.setMonth(d.getMonth() + 3);
  document.getElementById('target-date').value = d.toISOString().split('T')[0];

  // Forms Listeners
  document.getElementById('create-goal-form').addEventListener('submit', handleCreateGoal);
  document.getElementById('move-money-form').addEventListener('submit', handleMoveMoney);
  document.getElementById('withdraw-form').addEventListener('submit', handleWithdrawMoney);

  // Transaction Filters
  document.getElementById('tx-search').addEventListener('input', filterTransactions);
  document.getElementById('tx-type-filter').addEventListener('change', filterTransactions);
});

async function loadSavingsData() {
  try {
    // 1. Fetch Savings Summary
    const summaryRes = await ApiClient.get('/api/savings/summary');
    if (summaryRes.success && summaryRes.data) {
      const s = summaryRes.data;
      document.getElementById('metric-total-saved').textContent = formatCurrency(s.totalSavedAmount);
      document.getElementById('metric-wallet-balance').textContent = formatCurrency(s.availableWalletBalance);
      document.getElementById('metric-active-goals').textContent = s.activeSavingsGoals;
      document.getElementById('metric-completed-goals').textContent = s.completedGoals;
      walletBalance = s.availableWalletBalance || 0;
    }

    // 2. Fetch Goals List
    const goalsRes = await ApiClient.get('/api/savings/goals');
    if (goalsRes.success && goalsRes.data) {
      userGoals = goalsRes.data;
      renderGoals(userGoals);
      populateGoalDropdowns(userGoals);
    }

    // 3. Fetch Transactions
    const txRes = await ApiClient.get('/api/savings/transactions');
    if (txRes.success && txRes.data) {
      savingsTransactions = txRes.data;
      renderTransactions(savingsTransactions);
    }
  } catch (err) {
    showToast('Error loading savings module: ' + err.message, 'error');
  }
}

function renderGoals(goals) {
  const container = document.getElementById('goals-container');
  if (!goals || goals.length === 0) {
    container.innerHTML = `
      <div class="empty-state" style="grid-column: 1 / -1;">
        <div class="empty-state-icon"><i class="fa-solid fa-piggy-bank"></i></div>
        <h3>Start Your First Savings Goal</h3>
        <p style="color: var(--text-muted); max-width: 400px; margin: 0.5rem auto 1.5rem auto;">
          Create a goal and start saving toward something important like a new laptop, emergency fund, or travel.
        </p>
        <button class="btn btn-primary" onclick="openModal('create-goal-modal')">
          <i class="fa-solid fa-plus"></i> Create Savings Goal
        </button>
      </div>
    `;
    return;
  }

  container.innerHTML = goals.map(goal => {
    const saved = goal.savedAmount || 0;
    const target = goal.targetAmount || 1;
    const pct = Math.min(100, Math.round((saved / target) * 1000) / 10);
    const remaining = Math.max(0, target - saved);
    const isCompleted = goal.status === 'COMPLETED' || saved >= target;

    let categoryIcon = 'fa-sack-dollar';
    if (goal.category === 'Laptop') categoryIcon = 'fa-laptop';
    if (goal.category === 'Emergency Fund') categoryIcon = 'fa-shield-heart';
    if (goal.category === 'Education') categoryIcon = 'fa-graduation-cap';
    if (goal.category === 'Travel') categoryIcon = 'fa-plane';
    if (goal.category === 'New Phone') categoryIcon = 'fa-mobile-screen-button';
    if (goal.category === 'Vehicle') categoryIcon = 'fa-car';
    if (goal.category === 'Home') categoryIcon = 'fa-house-user';
    if (goal.category === 'Wedding') categoryIcon = 'fa-ring';
    if (goal.category === 'Investment') categoryIcon = 'fa-chart-line';

    return `
      <div class="glass-card goal-card">
        <div class="goal-card-header">
          <div style="display: flex; align-items: center; gap: 0.75rem;">
            <div class="goal-icon"><i class="fa-solid ${categoryIcon}"></i></div>
            <div>
              <div style="font-weight: 700; font-size: 1.1rem;">${goal.goalName}</div>
              <div style="font-size: 0.75rem; color: var(--text-muted);">${goal.category}</div>
            </div>
          </div>
          <span class="badge ${isCompleted ? 'badge-success' : 'badge-info'}">
            ${isCompleted ? '🎉 COMPLETED' : 'ACTIVE'}
          </span>
        </div>

        ${goal.description ? `<p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.75rem;">${goal.description}</p>` : ''}

        <div class="goal-amounts">
          <div>
            <div class="goal-saved">${formatCurrency(saved)}</div>
            <div class="goal-target">Target: ${formatCurrency(target)}</div>
          </div>
          <div style="text-align: right;">
            <div style="font-weight: 800; font-size: 1.1rem; color: ${isCompleted ? 'var(--secondary)' : 'var(--primary)'};">${pct}%</div>
            <div style="font-size: 0.75rem; color: var(--text-muted);">Saved</div>
          </div>
        </div>

        <!-- Visual Progress Bar -->
        <div class="progress-bar-bg" style="margin: 0.5rem 0 1rem 0;">
          <div class="progress-bar-fill ${isCompleted ? 'completed' : ''}" style="width: ${pct}%;"></div>
        </div>

        <div style="display: flex; justify-content: space-between; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.5rem;">
          <span>Remaining: <strong style="color: var(--text-main);">${formatCurrency(remaining)}</strong></span>
          <span>Target Date: <strong>${goal.targetDate || 'No date'}</strong></span>
        </div>

        <div class="goal-actions">
          <button class="btn btn-secondary btn-sm" style="flex: 1;" onclick="openMoveMoneyForGoal(${goal.id})">
            <i class="fa-solid fa-plus"></i> Add Money
          </button>
          <button class="btn btn-outline btn-sm" style="flex: 1;" onclick="openWithdrawForGoal(${goal.id})">
            <i class="fa-solid fa-arrow-up-from-bracket"></i> Withdraw
          </button>
          <button class="btn btn-outline btn-sm" style="padding: 0.4rem 0.6rem; color: var(--danger); border-color: var(--border-color);" title="Delete Goal" onclick="deleteGoal(${goal.id})">
            <i class="fa-solid fa-trash"></i>
          </button>
        </div>
      </div>
    `;
  }).join('');
}

function populateGoalDropdowns(goals) {
  const moveSelect = document.getElementById('move-goal-select');
  const withdrawSelect = document.getElementById('withdraw-goal-select');

  if (!goals || goals.length === 0) {
    moveSelect.innerHTML = `<option value="">No active goals found</option>`;
    withdrawSelect.innerHTML = `<option value="">No goals with funds found</option>`;
    return;
  }

  moveSelect.innerHTML = goals.map(g => `<option value="${g.id}">${g.goalName} (Current Saved: ${formatCurrency(g.savedAmount)})</option>`).join('');
  withdrawSelect.innerHTML = goals.map(g => `<option value="${g.id}">${g.goalName} (Saved: ${formatCurrency(g.savedAmount)})</option>`).join('');

  updateWithdrawMax();
}

function calculateMoveRemaining() {
  const amtInput = parseFloat(document.getElementById('move-amount').value) || 0;
  const currentWalletElem = document.getElementById('move-current-wallet');
  const remainingWalletElem = document.getElementById('move-remaining-wallet');
  
  currentWalletElem.textContent = formatCurrency(walletBalance);
  const rem = walletBalance - amtInput;
  remainingWalletElem.textContent = formatCurrency(Math.max(0, rem));

  if (amtInput > walletBalance) {
    remainingWalletElem.style.color = 'var(--danger)';
  } else {
    remainingWalletElem.style.color = 'var(--text-main)';
  }
}

function updateWithdrawMax() {
  const select = document.getElementById('withdraw-goal-select');
  const goalId = parseInt(select.value);
  const goal = userGoals.find(g => g.id === goalId);
  const savedElem = document.getElementById('withdraw-saved-amount');
  if (goal) {
    savedElem.textContent = formatCurrency(goal.savedAmount);
  } else {
    savedElem.textContent = formatCurrency(0);
  }
}

function openMoveMoneyModal() {
  document.getElementById('move-current-wallet').textContent = formatCurrency(walletBalance);
  document.getElementById('move-remaining-wallet').textContent = formatCurrency(walletBalance);
  openModal('move-money-modal');
}

function openMoveMoneyForGoal(goalId) {
  const select = document.getElementById('move-goal-select');
  select.value = goalId;
  openMoveMoneyModal();
}

function openWithdrawForGoal(goalId) {
  const select = document.getElementById('withdraw-goal-select');
  select.value = goalId;
  updateWithdrawMax();
  openModal('withdraw-modal');
}

async function handleCreateGoal(e) {
  e.preventDefault();
  const goalName = document.getElementById('goal-name').value;
  const category = document.getElementById('goal-category').value;
  const targetAmount = parseFloat(document.getElementById('target-amount').value);
  const initialSavingAmount = parseFloat(document.getElementById('initial-saving').value) || 0;
  const targetDate = document.getElementById('target-date').value;
  const description = document.getElementById('goal-desc').value;
  const pin = document.getElementById('goal-pin').value;

  if (initialSavingAmount > 0) {
    if (initialSavingAmount > walletBalance) {
      showToast('Initial saving amount cannot exceed wallet balance', 'error');
      return;
    }
    if (!pin) {
      showToast('Transaction PIN is required for initial deposit', 'error');
      return;
    }
  }

  try {
    const payload = { goalName, category, targetAmount, initialSavingAmount, targetDate, description, pin };
    const res = await ApiClient.post('/api/savings/goals', payload);
    if (res.success) {
      showToast('Savings goal created successfully!', 'success');
      closeModal('create-goal-modal');
      document.getElementById('create-goal-form').reset();
      loadSavingsData();

      if (res.data && res.data.status === 'COMPLETED') {
        document.getElementById('completed-goal-title').textContent = `You reached your goal for ${res.data.goalName}!`;
        openModal('goal-completion-modal');
      }
    }
  } catch (err) {
    showToast(err.message || 'Failed to create goal', 'error');
  }
}

async function handleMoveMoney(e) {
  e.preventDefault();
  const goalId = document.getElementById('move-goal-select').value;
  const amount = parseFloat(document.getElementById('move-amount').value);
  const pin = document.getElementById('move-pin').value;

  if (!goalId) {
    showToast('Please select a savings goal', 'error');
    return;
  }
  if (!amount || amount <= 0) {
    showToast('Amount must be greater than 0', 'error');
    return;
  }
  if (amount > walletBalance) {
    showToast('Amount cannot exceed available wallet balance', 'error');
    return;
  }

  const goal = userGoals.find(g => g.id == goalId);
  const confirmMsg = `Are you sure you want to move ${formatCurrency(amount)} from your wallet to ${goal ? goal.goalName : 'savings'}?`;
  if (!confirm(confirmMsg)) return;

  try {
    const res = await ApiClient.post(`/api/savings/goals/${goalId}/add-money`, { amount, pin });
    if (res.success) {
      showToast(`₹${amount} moved to your ${goal ? goal.goalName : ''} savings goal.`, 'success');
      closeModal('move-money-modal');
      document.getElementById('move-money-form').reset();
      loadSavingsData();

      if (res.data && res.data.status === 'COMPLETED') {
        document.getElementById('completed-goal-title').textContent = `You reached your goal for ${res.data.goalName}!`;
        openModal('goal-completion-modal');
      }
    }
  } catch (err) {
    showToast(err.message || 'Transfer failed', 'error');
  }
}

async function handleWithdrawMoney(e) {
  e.preventDefault();
  const goalId = document.getElementById('withdraw-goal-select').value;
  const amount = parseFloat(document.getElementById('withdraw-amount').value);
  const pin = document.getElementById('withdraw-pin').value;

  if (!goalId) {
    showToast('Please select a savings goal', 'error');
    return;
  }
  const goal = userGoals.find(g => g.id == goalId);
  if (!amount || amount <= 0) {
    showToast('Withdrawal amount must be greater than 0', 'error');
    return;
  }
  if (goal && amount > goal.savedAmount) {
    showToast('Withdrawal amount cannot exceed saved amount in this goal', 'error');
    return;
  }

  try {
    const res = await ApiClient.post(`/api/savings/goals/${goalId}/withdraw`, { amount, pin });
    if (res.success) {
      showToast(`₹${amount} withdrawn from your savings and added to your wallet.`, 'success');
      closeModal('withdraw-modal');
      document.getElementById('withdraw-form').reset();
      loadSavingsData();
    }
  } catch (err) {
    showToast(err.message || 'Withdrawal failed', 'error');
  }
}

async function deleteGoal(goalId) {
  const goal = userGoals.find(g => g.id === goalId);
  if (!confirm(`Are you sure you want to delete goal "${goal ? goal.goalName : ''}"? Any saved money will be returned to your wallet.`)) return;

  try {
    const res = await ApiClient.delete(`/api/savings/goals/${goalId}`);
    if (res.success) {
      showToast('Goal deleted and funds refunded to wallet', 'success');
      loadSavingsData();
    }
  } catch (err) {
    showToast(err.message || 'Failed to delete goal', 'error');
  }
}

function renderTransactions(txs) {
  const tbody = document.getElementById('savings-tx-tbody');
  if (!txs || txs.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="padding: 1.5rem; text-align: center; color: var(--text-muted);">No savings transactions recorded yet.</td></tr>`;
    return;
  }

  tbody.innerHTML = txs.map(tx => `
    <tr style="border-bottom: 1px solid var(--border-color);">
      <td style="padding: 0.75rem; font-family: monospace; font-size: 0.85rem; color: var(--primary);">${tx.transactionId}</td>
      <td style="padding: 0.75rem; font-size: 0.8rem; color: var(--text-muted);">${tx.createdAt ? new Date(tx.createdAt).toLocaleString() : ''}</td>
      <td style="padding: 0.75rem; font-weight: 600;">${tx.note || 'Savings Goal'}</td>
      <td style="padding: 0.75rem;">
        <span class="badge ${tx.type === 'SAVINGS_TRANSFER' ? 'badge-info' : 'badge-success'}" style="font-size: 0.7rem;">
          ${tx.type}
        </span>
      </td>
      <td style="padding: 0.75rem; font-weight: 700; color: ${tx.type === 'SAVINGS_TRANSFER' ? 'var(--primary)' : 'var(--secondary)'};">
        ${tx.type === 'SAVINGS_TRANSFER' ? '-' : '+'}${formatCurrency(tx.amount)}
      </td>
      <td style="padding: 0.75rem;">
        <span class="badge badge-success" style="font-size: 0.7rem;">SUCCESS</span>
      </td>
    </tr>
  `).join('');
}

function filterTransactions() {
  const query = document.getElementById('tx-search').value.toLowerCase();
  const typeFilter = document.getElementById('tx-type-filter').value;

  const filtered = savingsTransactions.filter(tx => {
    const matchesQuery = (tx.transactionId && tx.transactionId.toLowerCase().includes(query)) ||
                         (tx.note && tx.note.toLowerCase().includes(query));
    const matchesType = typeFilter === 'ALL' || tx.type === typeFilter;
    return matchesQuery && matchesType;
  });

  renderTransactions(filtered);
}
