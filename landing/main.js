// NullVoid Launcher – Landing Page Scripts

// ── Constants ───────────────────────────────────────────────────

const GH_REPO = 'codershubinc/Null-Void-Launcher';
const GH_API  = 'https://api.github.com/repos/' + GH_REPO + '/releases/latest';
const GH_REPO_API = 'https://api.github.com/repos/' + GH_REPO;

let cachedRelease  = null;

// ── GitHub Repo Stars Fetch ─────────────────────────────────────

function fetchRepoStars() {
  var countEl = document.getElementById('gh-stars-count');
  if (!countEl) return;

  fetch(GH_REPO_API)
    .then(function (res) {
      if (!res.ok) throw new Error('HTTP ' + res.status);
      return res.json();
    })
    .then(function (data) {
      if (typeof data.stargazers_count === 'number') {
        countEl.textContent = formatNumber(data.stargazers_count);
      }
    })
    .catch(function () {
      // Fallback star count representation
      countEl.textContent = '★';
    });
}

// ── Floating Island Navbar on Scroll ────────────────────────────

function initNavbarScroll() {
  var header = document.getElementById('site-header');
  if (!header) return;

  var handleScroll = function () {
    if (window.scrollY > 40) {
      header.classList.add('is-scrolled');
    } else {
      header.classList.remove('is-scrolled');
    }
  };

  window.addEventListener('scroll', handleScroll, { passive: true });
  handleScroll(); // Initial check
}

// ── Release Modal ───────────────────────────────────────────────

function openReleaseModal() {
  var modal = document.getElementById('release-modal');
  if (!modal) return;
  modal.hidden = false;
  // trigger transition
  requestAnimationFrame(function () {
    requestAnimationFrame(function () {
      modal.classList.add('is-visible');
    });
  });
  document.addEventListener('keydown', onModalEsc);
  modal.addEventListener('click', onBackdropClick);

  if (cachedRelease) {
    renderReleaseInfo(cachedRelease);
  } else {
    fetchLatestRelease();
  }

  if (window.lucide) window.lucide.createIcons();
}

function closeReleaseModal() {
  var modal = document.getElementById('release-modal');
  if (!modal) return;
  modal.classList.remove('is-visible');
  document.removeEventListener('keydown', onModalEsc);
  modal.removeEventListener('click', onBackdropClick);
  setTimeout(function () {
    modal.hidden = true;
  }, 260);
}

function onModalEsc(e) {
  if (e.key === 'Escape') closeReleaseModal();
}

function onBackdropClick(e) {
  if (e.target.classList.contains('modal-backdrop')) closeReleaseModal();
}

const LOCAL_FALLBACK_RELEASE = {
  tag_name: 'v0.1.5',
  name: 'NullVoid Launcher v0.1.5 (Singularity)',
  published_at: '2026-11-01T00:00:00Z',
  body: 'Singularity (Build 7) • Android 10–15 (SDK 29–37)\n• Favorite Apps vanishing on package updates resolution\n• Music player enhancements & Settings UI overhaul\n• Hardware telemetry with glassmorphism UI',
  html_url: 'https://github.com/codershubinc/Null-Void-Launcher/releases/latest',
  assets: [
    {
      name: 'NullVoidLauncher-v0.1.5.apk',
      size: 15728640,
      download_count: 0,
      browser_download_url: 'https://github.com/codershubinc/Null-Void-Launcher/releases/latest'
    }
  ]
};

function fetchLatestRelease() {
  var body = document.getElementById('modal-body');
  body.innerHTML =
    '<div class="modal-loading">' +
    '<span class="spinner" aria-hidden="true"></span>' +
    '<span>Fetching latest release…</span></div>';

  fetch(GH_API)
    .then(function (res) {
      if (!res.ok) throw new Error('HTTP ' + res.status);
      return res.json();
    })
    .then(function (data) {
      cachedRelease = data;
      renderReleaseInfo(data);
    })
    .catch(function () {
      // Fallback gracefully using RELEASE metadata if network or rate-limited
      cachedRelease = LOCAL_FALLBACK_RELEASE;
      renderReleaseInfo(LOCAL_FALLBACK_RELEASE);
    });
}

function renderReleaseInfo(data) {
  var body   = document.getElementById('modal-body');
  var footer = document.getElementById('modal-footer');

  // Find .apk asset
  var apk = null;
  var assets = data.assets || [];
  for (var i = 0; i < assets.length; i++) {
    if (assets[i].name && assets[i].name.endsWith('.apk')) {
      apk = assets[i];
      break;
    }
  }

  var tagName     = data.tag_name || '';
  var releaseName = data.name || 'NullVoid Launcher';
  var sizeStr     = apk ? formatBytes(apk.size) : '—';
  var downloads   = 0;
  for (var j = 0; j < assets.length; j++) {
    downloads += assets[j].download_count || 0;
  }

  body.innerHTML =
    '<div class="release-info">' +
      '<div class="release-header-island">' +
        '<div class="release-title-row">' +
          '<span class="release-title-text">' + escHtml(releaseName) + '</span>' +
          (tagName ? '<span class="release-version-chip">' + escHtml(tagName) + '</span>' : '') +
        '</div>' +
      '</div>' +
      '<div class="release-islands-row">' +
        '<div class="stat-island">' +
          '<i data-lucide="hard-drive" class="island-icon" width="16" height="16" aria-hidden="true"></i>' +
          '<div class="island-meta">' +
            '<span class="island-label">APK Size</span>' +
            '<span class="island-value">' + sizeStr + '</span>' +
          '</div>' +
        '</div>' +
        '<div class="stat-island">' +
          '<i data-lucide="download" class="island-icon" width="16" height="16" aria-hidden="true"></i>' +
          '<div class="island-meta">' +
            '<span class="island-label">Downloads</span>' +
            '<span class="island-value">' + formatNumber(downloads) + '</span>' +
          '</div>' +
        '</div>' +
      '</div>' +
    '</div>';

  // Footer
  footer.hidden = false;
  var dlBtn  = document.getElementById('modal-download');
  var ghLink = document.getElementById('modal-gh-link');
  if (apk && dlBtn) {
    dlBtn.href = apk.browser_download_url;
    dlBtn.querySelector('span').textContent = 'Download APK (' + sizeStr + ')';
  } else if (dlBtn) {
    dlBtn.href = 'https://github.com/' + GH_REPO + '/releases/latest';
    dlBtn.querySelector('span').textContent = 'Download APK';
  }
  if (ghLink) {
    ghLink.href = data.html_url || ('https://github.com/' + GH_REPO + '/releases/latest');
  }

  if (window.lucide) window.lucide.createIcons();
}

// ── Helpers ─────────────────────────────────────────────────────

function formatBytes(bytes) {
  if (!bytes || bytes === 0) return '—';
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / 1048576).toFixed(1) + ' MB';
}

function formatNumber(n) {
  if (!n) return '0';
  return n.toLocaleString('en-US');
}

function escHtml(str) {
  var el = document.createElement('span');
  el.textContent = str;
  return el.innerHTML;
}

// ── Device Screen Carousel ──────────────────────────────────────

var currentSlide = 0;
var totalSlides  = 3;
var autoplayTimer = null;

function updateCarouselUI() {
  var track = document.getElementById('carousel-track');
  if (track) {
    track.style.transform = 'translateX(-' + (currentSlide * 100) + '%)';
  }

  // Update Top Tabs
  var tabIds = ['tab-home', 'tab-telemetry', 'tab-drawer'];
  tabIds.forEach(function (id, idx) {
    var tab = document.getElementById(id);
    if (tab) {
      tab.setAttribute('aria-selected', idx === currentSlide ? 'true' : 'false');
    }
  });

  // Update Dots
  var dots = document.querySelectorAll('.carousel-dot');
  dots.forEach(function (dot, idx) {
    if (idx === currentSlide) {
      dot.classList.add('is-active');
    } else {
      dot.classList.remove('is-active');
    }
  });
}

function goToSlide(index) {
  currentSlide = (index + totalSlides) % totalSlides;
  updateCarouselUI();
  resetAutoplay();
}

function nextSlide() {
  goToSlide(currentSlide + 1);
}

function prevSlide() {
  goToSlide(currentSlide - 1);
}

function startAutoplay() {
  stopAutoplay();
  autoplayTimer = setInterval(function () {
    currentSlide = (currentSlide + 1) % totalSlides;
    updateCarouselUI();
  }, 4200);
}

function stopAutoplay() {
  if (autoplayTimer) {
    clearInterval(autoplayTimer);
    autoplayTimer = null;
  }
}

function resetAutoplay() {
  stopAutoplay();
  startAutoplay();
}

function initCarouselGestures() {
  var track = document.getElementById('carousel-track');
  var stage = document.querySelector('.carousel-wrapper');
  if (!track) return;

  var startX = 0;
  var currentX = 0;
  var isSwiping = false;

  track.addEventListener('touchstart', function (e) {
    stopAutoplay();
    startX = e.touches[0].clientX;
    isSwiping = true;
  }, { passive: true });

  track.addEventListener('touchmove', function (e) {
    if (!isSwiping) return;
    currentX = e.touches[0].clientX;
  }, { passive: true });

  track.addEventListener('touchend', function () {
    if (!isSwiping) return;
    isSwiping = false;
    var deltaX = currentX - startX;
    if (Math.abs(deltaX) > 40) {
      if (deltaX < 0) nextSlide();
      else prevSlide();
    } else {
      startAutoplay();
    }
  });

  if (stage) {
    stage.addEventListener('mouseenter', stopAutoplay);
    stage.addEventListener('mouseleave', startAutoplay);
  }
}

function focusPreview() {
  var stage = document.getElementById('device-preview');
  if (stage) {
    stage.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
}

// ── Scroll Reveal (feature tiles) ───────────────────────────────

function initScrollReveal() {
  var tiles = document.querySelectorAll('.feature-tile');
  if (!('IntersectionObserver' in window)) {
    tiles.forEach(function (t) { t.classList.add('is-visible'); });
    return;
  }
  var observer = new IntersectionObserver(
    function (entries) {
      entries.forEach(function (entry) {
        if (entry.isIntersecting) {
          entry.target.classList.add('is-visible');
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.1 }
  );

  tiles.forEach(function (tile, idx) {
    tile.style.transitionDelay = idx * 90 + 'ms';
    observer.observe(tile);
  });
}

// ── Bootstrap ───────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', function () {
  initNavbarScroll();
  fetchRepoStars();
  initScrollReveal();
  initCarouselGestures();
  startAutoplay();
  if (window.lucide) window.lucide.createIcons();
});
