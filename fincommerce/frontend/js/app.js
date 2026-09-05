/* FinCommerce Common UI & Utilities */

function showToast(message, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  let iconClass = 'fa-circle-info';
  if (type === 'success') iconClass = 'fa-circle-check';
  if (type === 'error') iconClass = 'fa-triangle-exclamation';

  toast.innerHTML = `
    <i class="fa-solid ${iconClass}"></i>
    <div>${message}</div>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add('active');
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove('active');
  }
}

// Global Event Delegation for Close Buttons
document.addEventListener('click', (e) => {
  if (e.target.classList.contains('modal-close') || e.target.classList.contains('modal-overlay')) {
    const modal = e.target.closest('.modal-overlay');
    if (modal) modal.classList.remove('active');
  }
});

// Format Currency Utility
function formatCurrency(amount) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2
  }).format(amount || 0);
}

// Render Sidebar Navigation automatically if element exists
function renderSidebar(activePage = 'dashboard') {
  const user = Auth.getCurrentUser();
  const sidebarContainer = document.getElementById('sidebar-component');
  if (!sidebarContainer) return;

  const isAdmin = user && user.role === 'ROLE_ADMIN';

  sidebarContainer.innerHTML = `
    <aside class="sidebar">
      <div class="sidebar-brand">
        <i class="fa-solid fa-wallet"></i>
        <span>FinCommerce</span>
      </div>
      <ul class="sidebar-menu">
        <li class="sidebar-item">
          <a href="dashboard.html" class="sidebar-link ${activePage === 'dashboard' ? 'active' : ''}">
            <i class="fa-solid fa-house-chimney"></i> Dashboard
          </a>
        </li>
        <li class="sidebar-item">
          <a href="wallet.html" class="sidebar-link ${activePage === 'wallet' ? 'active' : ''}">
            <i class="fa-solid fa-wallet"></i> Digital Wallet
          </a>
        </li>
        <li class="sidebar-item">
          <a href="savings.html" class="sidebar-link ${activePage === 'savings' ? 'active' : ''}">
            <i class="fa-solid fa-piggy-bank"></i> Savings Goals
          </a>
        </li>
        <li class="sidebar-item">
          <a href="shop.html" class="sidebar-link ${activePage === 'shop' ? 'active' : ''}">
            <i class="fa-solid fa-store"></i> Shop Platform
          </a>
        </li>
        <li class="sidebar-item">
          <a href="cart.html" class="sidebar-link ${activePage === 'cart' ? 'active' : ''}">
            <i class="fa-solid fa-cart-shopping"></i> Shopping Cart
          </a>
        </li>
        <li class="sidebar-item">
          <a href="orders.html" class="sidebar-link ${activePage === 'orders' ? 'active' : ''}">
            <i class="fa-solid fa-box-open"></i> Orders & Tracking
          </a>
        </li>
        <li class="sidebar-item">
          <a href="transactions.html" class="sidebar-link ${activePage === 'transactions' ? 'active' : ''}">
            <i class="fa-solid fa-receipt"></i> Transactions
          </a>
        </li>
        <li class="sidebar-item">
          <a href="profile.html" class="sidebar-link ${activePage === 'profile' ? 'active' : ''}">
            <i class="fa-solid fa-user-gears"></i> User Profile
          </a>
        </li>
        <li class="sidebar-item">
          <a href="settings.html" class="sidebar-link ${activePage === 'settings' ? 'active' : ''}">
            <i class="fa-solid fa-shield-halved"></i> Security Center
          </a>
        </li>
        <li class="sidebar-item">
          <a href="support.html" class="sidebar-link ${activePage === 'support' ? 'active' : ''}">
            <i class="fa-solid fa-headset"></i> Support & FAQ
          </a>
        </li>
        ${isAdmin ? `
        <li class="sidebar-item" style="margin-top: 1rem; border-top: 1px solid var(--border-color); padding-top: 0.5rem;">
          <a href="admin.html" class="sidebar-link ${activePage === 'admin' ? 'active' : ''}" style="color: var(--accent);">
            <i class="fa-solid fa-user-shield"></i> Admin Portal
          </a>
        </li>
        ` : ''}
      </ul>
      <div class="sidebar-user">
        <img src="${user ? user.profilePhoto || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80' : ''}" class="user-avatar" alt="Avatar">
        <div style="flex: 1; overflow: hidden;">
          <div style="font-weight: 600; font-size: 0.9rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${user ? user.fullName : 'Guest'}</div>
          <div style="font-size: 0.75rem; color: var(--text-muted);">${user ? user.email : ''}</div>
        </div>
        <button onclick="Auth.logout()" class="btn btn-outline btn-sm" title="Logout" style="padding: 0.4rem;"><i class="fa-solid fa-right-from-bracket"></i></button>
      </div>
    </aside>
  `;
  updateNotificationBadge();
}

async function updateNotificationBadge() {
  const badges = document.querySelectorAll('.notification-badge-count');
  if (badges.length === 0) return;
  try {
    const res = await ApiClient.get('/api/notifications/unread-count');
    if (res && res.success) {
      badges.forEach(b => {
        if (res.data > 0) {
          b.textContent = res.data;
          b.style.display = 'inline-flex';
        } else {
          b.style.display = 'none';
        }
      });
    }
  } catch(e) {}
}

