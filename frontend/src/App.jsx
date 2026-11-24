import { useEffect, useMemo, useState } from 'react'
import { api, getToken, setToken } from './api'
import { Activity, ArrowDownLeft, ArrowLeftRight, ArrowUpRight, Bot, CreditCard, LogOut, Plus, RefreshCw, ShieldCheck, Wallet } from 'lucide-react'

const money = n => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 }).format(Number(n || 0))
function App() {
  const [token, setAuth] = useState(getToken())
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [profile, setProfile] = useState(null), [accounts, setAccounts] = useState([]), [transactions, setTransactions] = useState([])
  const [busy, setBusy] = useState(false), [notice, setNotice] = useState(''), [error, setError] = useState('')
  const [accountType, setAccountType] = useState('SAVINGS')
  const [selected, setSelected] = useState(''), [amount, setAmount] = useState(''), [description, setDescription] = useState('')
  const [toAccount, setToAccount] = useState(''), [insights, setInsights] = useState(null)
  const total = useMemo(() => accounts.reduce((s,a) => s + Number(a.balance), 0), [accounts])
  async function load() {
    const [p,a,t] = await Promise.all([api('/customers/me'), api('/accounts'), api('/transactions')])
    setProfile(p); setAccounts(a); setTransactions(t); if (!selected && a.length) setSelected(String(a[0].id))
  }
  useEffect(() => { if (token) load().catch(e => { setError(e.message); if (String(e.message).includes('401')) logout() }) }, [token])
  function logout() { setToken(null); setAuth(null); setProfile(null); setAccounts([]); setTransactions([]); setInsights(null) }
  async function submitAuth(e) {
    e.preventDefault(); setBusy(true); setError('')
    try {
      const data = await api(`/auth/${mode === 'register' ? 'register' : 'login'}`, { method:'POST', body:JSON.stringify(form) })
      setToken(data.token); setAuth(data.token); setNotice(`Welcome ${data.fullName}`)
    } catch(e) { setError(e.message) } finally { setBusy(false) }
  }
  async function createAccount() {
    setBusy(true); setError('')
    try { await api('/accounts', { method:'POST', body:JSON.stringify({accountType}) }); await load(); setNotice('Account opened successfully') }
    catch(e) { setError(e.message) } finally { setBusy(false) }
  }
  async function moneyAction(kind) {
    if (!selected) return setError('Please open an account first')
    setBusy(true); setError('')
    try {
      await api(`/accounts/${selected}/${kind}`, { method:'POST', body:JSON.stringify({amount:Number(amount),description}) })
      setAmount(''); setDescription(''); await load(); setNotice(kind === 'deposit' ? 'Deposit completed' : 'Withdrawal completed')
    } catch(e) { setError(e.message) } finally { setBusy(false) }
  }
  async function transfer() {
    setBusy(true); setError('')
    try {
      await api('/transfers', { method:'POST', body:JSON.stringify({fromAccountId:Number(selected),toAccountId:Number(toAccount),amount:Number(amount),description}) })
      setAmount(''); setDescription(''); await load(); setNotice('Transfer completed')
    } catch(e) { setError(e.message) } finally { setBusy(false) }
  }
  async function getInsights() {
    setBusy(true); setError('')
    try { setInsights(await api('/ai/insights',{method:'POST'})) } catch(e) { setError(e.message) } finally { setBusy(false) }
  }
  if (!token) return <main className="auth-shell"><div className="auth-card"><div className="brand-mark"><Wallet size={25}/></div><p className="eyebrow">SMARTBANK DIGITAL</p><h1>{mode === 'login' ? 'Welcome back' : 'Create your account'}</h1><p className="muted">A smarter way to manage your money.</p>
    <form onSubmit={submitAuth}>{mode === 'register' && <label>Full name<input required value={form.fullName} onChange={e=>setForm({...form,fullName:e.target.value})} placeholder="Your name"/></label>}
      <label>Email address<input type="email" required value={form.email} onChange={e=>setForm({...form,email:e.target.value})} placeholder="you@example.com"/></label>
      <label>Password<input type="password" required minLength="8" value={form.password} onChange={e=>setForm({...form,password:e.target.value})} placeholder="At least 8 characters"/></label>
      <button className="primary full" disabled={busy}>{busy?'Please wait…':mode==='login'?'Sign in':'Create account'}</button></form>
    <p className="switch">{mode==='login'?"New to SmartBank?":"Already registered?"} <button className="link" onClick={()=>setMode(mode==='login'?'register':'login')}>{mode==='login'?'Create an account':'Sign in'}</button></p>
    {error && <p className="alert error">{error}</p>}</div><p className="auth-foot"><ShieldCheck size={14}/> Demo environment · Never enter real banking credentials</p></main>
  return <div className="app-shell"><aside className="sidebar"><div className="logo"><span className="brand-mark"><Wallet/></span><b>SmartBank</b></div><div className="side-label">WORKSPACE</div><div className="nav active"><Activity size={17}/> Overview</div><div className="side-label side-bottom">SECURE SESSION</div><div className="user-mini"><div className="avatar">{profile?.fullName?.[0]||'S'}</div><div><b>{profile?.fullName||'Customer'}</b><small>{profile?.email}</small></div><button className="icon-btn" title="Sign out" onClick={logout}><LogOut size={17}/></button></div></aside>
    <main className="main"><header><div><p className="eyebrow">PERSONAL BANKING</p><h1>Good day, {profile?.fullName?.split(' ')[0]||'there'} 👋</h1><p className="muted">Here’s your financial snapshot.</p></div><button className="ghost" onClick={()=>load()}><RefreshCw size={16}/> Refresh</button></header>
    {notice && <div className="alert success">{notice}<button onClick={()=>setNotice('')}>×</button></div>}{error && <div className="alert error">{error}<button onClick={()=>setError('')}>×</button></div>}
    <section className="stats"><div className="stat-card balance"><div className="stat-top"><span>Total balance</span><Wallet size={18}/></div><h2>{money(total)}</h2><small>Across {accounts.length} account(s)</small><div className="balance-glow"/></div><div className="stat-card"><div className="stat-top"><span>Bank accounts</span><CreditCard size={18}/></div><h2>{accounts.length}</h2><small>Active accounts</small></div><div className="stat-card"><div className="stat-top"><span>Transactions</span><Activity size={18}/></div><h2>{transactions.length}</h2><small>Recorded activity</small></div></section>
    <section className="grid-two"><div className="panel"><div className="panel-head"><div><h3>Your accounts</h3><p className="muted">Manage your SmartBank accounts</p></div><button className="primary small" onClick={createAccount} disabled={busy}><Plus size={15}/> Open account</button></div>
      <div className="account-list">{accounts.length===0?<div className="empty">No accounts yet. Open your first account to get started.</div>:accounts.map(a=><button key={a.id} onClick={()=>setSelected(String(a.id))} className={`account-row ${selected===String(a.id)?'chosen':''}`}><div className="account-icon"><Wallet size={18}/></div><div className="account-info"><b>{a.accountType} account</b><small>•••• {a.accountNumber.slice(-4)} · {a.status}</small></div><strong>{money(a.balance)}</strong></button>)}</div>
      <div className="open-row"><select value={accountType} onChange={e=>setAccountType(e.target.value)}><option value="SAVINGS">Savings</option><option value="CURRENT">Current</option></select><span className="muted tiny">Choose account type before opening</span></div>
    </div><div className="panel"><div className="panel-head"><div><h3>Quick actions</h3><p className="muted">Deposit, withdraw or transfer</p></div><ArrowLeftRight size={19}/></div>
      <label className="compact-label">From account<select value={selected} onChange={e=>setSelected(e.target.value)}><option value="">Select account</option>{accounts.map(a=><option key={a.id} value={a.id}>{a.accountType} · {a.accountNumber}</option>)}</select></label>
      <label className="compact-label">Amount (INR)<input type="number" min="0.01" step="0.01" value={amount} onChange={e=>setAmount(e.target.value)} placeholder="Enter amount"/></label>
      <label className="compact-label">Description<input value={description} onChange={e=>setDescription(e.target.value)} placeholder="Optional note"/></label>
      <div className="action-row"><button className="secondary" disabled={busy||!amount} onClick={()=>moneyAction('deposit')}><ArrowDownLeft size={15}/> Deposit</button><button className="secondary" disabled={busy||!amount} onClick={()=>moneyAction('withdraw')}><ArrowUpRight size={15}/> Withdraw</button></div>
      <label className="compact-label">Transfer to account<select value={toAccount} onChange={e=>setToAccount(e.target.value)}><option value="">Select destination</option>{accounts.filter(a=>String(a.id)!==selected).map(a=><option key={a.id} value={a.id}>{a.accountType} · {a.accountNumber}</option>)}</select></label>
      <button className="primary full" disabled={busy||!amount||!selected||!toAccount} onClick={transfer}>Transfer money <ArrowLeftRight size={15}/></button>
    </div></section>
    <section className="grid-two lower"><div className="panel"><div className="panel-head"><div><h3>Recent transactions</h3><p className="muted">Latest account activity</p></div><Activity size={18}/></div>
      {transactions.length===0?<div className="empty">Your transactions will appear here.</div>:<div className="transactions">{transactions.slice(0,8).map(t=><div className="transaction" key={t.id}><div className={`tx-icon ${t.type.includes('IN')||t.type==='DEPOSIT'?'in':'out'}`}>{t.type.includes('IN')||t.type==='DEPOSIT'?<ArrowDownLeft size={17}/>:<ArrowUpRight size={17}/>}</div><div className="tx-info"><b>{t.type.replaceAll('_',' ')}</b><small>{t.description||'Bank transaction'} · {new Date(t.createdAt).toLocaleDateString()}</small></div><strong className={t.type.includes('IN')||t.type==='DEPOSIT'?'positive':'negative'}>{t.type.includes('IN')||t.type==='DEPOSIT'?'+':'−'}{money(t.amount)}</strong></div>)}</div>}
    </div><div className="panel ai-panel"><div className="ai-orb"><Bot size={22}/></div><p className="eyebrow">SMART INSIGHTS</p><h3>Your AI financial assistant</h3><p className="muted">Get general budgeting observations based on your transaction totals.</p><button className="primary full" onClick={getInsights} disabled={busy}><Bot size={16}/> Generate insights</button>
      {insights && <div className="insight-result">{typeof insights.insights==='string'?insights.insights:(insights.insights||[]).map((x,i)=><p key={i}>{x}</p>)}{insights.summary&&<small>Based on {insights.summary.length} transaction categories.</small>}</div>}
      <small className="disclaimer">Educational information only, not financial or investment advice.</small></div></section>
    <footer>SmartBank · Secure demo banking platform <span>Java · Spring Boot · React · MySQL</span></footer></main></div>
}
export default App
