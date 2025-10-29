<template>
  <footer
    class="bleckit-footer"
    :class="[`density-${density}`, positionClass]"
    :style="cssVars"
    role="contentinfo"
  >
    <div class="inner" :style="{ maxWidth: containerMaxWidth }">
      <div class="left">
        <p class="tagline" v-if="tagline">{{ tagline }}</p>
        <p class="desc" v-if="description">{{ description }}</p>
      </div>

      <a
        class="cta"
        :class="`btn-${buttonStyle}`"
        :href="finalLinkUrl"
        :target="linkTarget"
        rel="noopener noreferrer"
        :aria-label="`Open ${linkText}`"
      >
        <span>{{ linkText }}</span>
        <svg class="arrow" viewBox="0 0 24 24" aria-hidden="true">
          <path
            d="M7 17L17 7M17 7H10M17 7V14"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </a>
    </div>

    <div
      v-if="credits?.length"
      class="credits"
      :style="{ maxWidth: containerMaxWidth }"
    >
      <span class="credits-label">{{ creditsLabel }}</span>
      <span class="credits-list">
        <template v-for="(c, i) in credits" :key="i">
          <a
            class="credit"
            :href="c.url"
            :target="c.target || '_blank'"
            rel="noopener noreferrer"
          >
            {{ c.name }}
          </a>
          <span v-if="i < credits.length - 1" class="sep">·</span>
        </template>
      </span>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue';

type Density = 'compact' | 'comfortable';
type Position = 'static' | 'sticky' | 'fixed';
type ButtonStyle = 'filled' | 'outline' | 'ghost';
type Credit = { name: string; url: string; target?: string };

const props = withDefaults(
  defineProps<{
    // content
    tagline?: string;
    description?: string;
    linkText?: string;
    linkUrl?: string;
    linkTarget?: '_self' | '_blank';
    // credits
    credits?: Credit[] | null;
    creditsLabel?: string;
    // layout
    density?: Density;
    containerMaxWidth?: string;
    position?: Position;
    fixedAt?: 'bottom' | 'top';
    // visuals via props -> forwarded to CSS variables (optional)
    textColor?: string;
    secondaryTextColor?: string;
    bg?: string;
    border?: string;
    blurAmount?: string;
    btn?: string;
    btnHover?: string;
    btnBg?: string;
    btnText?: string;
    buttonStyle?: ButtonStyle;
    // link tracking
    appendUtm?: boolean;
    utmSource?: string;
    utmMedium?: string;
    utmCampaign?: string;
  }>(),
  {
    tagline: 'Built with care',
    description: 'Check out bleck.it for more of my work',
    linkText: 'Visit bleck.it',
    linkUrl: 'https://bleck.it',
    linkTarget: '_blank',
    credits: null,
    creditsLabel: 'With:',
    density: 'compact',
    containerMaxWidth: '1100px',
    position: 'static',
    fixedAt: 'bottom',
    blurAmount: '0px',
    buttonStyle: 'ghost',
    appendUtm: false,
    utmSource: 'bleckit-banner',
    utmMedium: 'referral',
    utmCampaign: 'brand',
  }
);

const positionClass = computed(() => {
  if (props.position === 'fixed') return `fixed-${props.fixedAt}`;
  if (props.position === 'sticky') return `sticky-${props.fixedAt}`;
  return 'pos-static';
});

const finalLinkUrl = computed(() => {
  if (!props.appendUtm) return props.linkUrl;
  try {
    const u = new URL(props.linkUrl);
    u.searchParams.set('utm_source', props.utmSource!);
    u.searchParams.set('utm_medium', props.utmMedium!);
    u.searchParams.set('utm_campaign', props.utmCampaign!);
    return u.toString();
  } catch {
    return props.linkUrl;
  }
});

/** Only set CSS vars when props provided; otherwise inherit from page */
const cssVars = computed(() => {
  const v: Record<string, string> = {};
  if (props.textColor) v['--bleckit-text'] = props.textColor;
  if (props.secondaryTextColor)
    v['--bleckit-text-2'] = props.secondaryTextColor;
  if (props.bg) v['--bleckit-bg'] = props.bg;
  if (props.border) v['--bleckit-border'] = props.border;
  if (props.blurAmount) v['--bleckit-blur'] = props.blurAmount;
  if (props.btn) v['--bleckit-btn'] = props.btn;
  if (props.btnHover) v['--bleckit-btn-hover'] = props.btnHover;
  if (props.btnBg) v['--bleckit-btn-bg'] = props.btnBg;
  if (props.btnText) v['--bleckit-btn-text'] = props.btnText;
  return v;
});
</script>

<style scoped>
/* No hardcoded colors. Everything inherits or reads CSS vars. */
.bleckit-footer {
  inset-inline: 0;
  color: var(--bleckit-text, currentColor);
  background: var(--bleckit-bg, transparent);
  backdrop-filter: blur(var(--bleckit-blur, 0px));
  border-top: 1px solid var(--bleckit-border, currentColor);
  padding: clamp(8px, 1.2vw, 12px) max(16px, 3.2vw);
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
  line-height: 1.35;
  z-index: 40;
  padding-bottom: calc(clamp(8px, 1.2vw, 12px) + env(safe-area-inset-bottom));
}

/* density */
.density-comfortable {
  font-size: 15px;
  padding-top: 14px;
  padding-bottom: 14px;
}

/* positioning */
.pos-static {
  position: static;
}
.fixed-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
}
.fixed-top {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  border-top: none;
  border-bottom: 1px solid var(--bleckit-border, currentColor);
}
.sticky-bottom {
  position: sticky;
  bottom: 0;
}
.sticky-top {
  position: sticky;
  top: 0;
  border-top: none;
  border-bottom: 1px solid var(--bleckit-border, currentColor);
}

.inner {
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: center;
  gap: 10px 16px;
  width: 100%;
}

.left {
  min-width: 0;
}
.tagline {
  margin: 0;
  font-weight: 600;
  letter-spacing: 0.2px;
  font-size: 0.92em;
}
.desc {
  margin: 0;
  color: var(--bleckit-text-2, currentColor);
  opacity: 0.9;
  font-size: 0.92em;
}

/* CTA button — styles are variable-driven */
.cta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  border-radius: 10px;
  border: 1.5px solid transparent;
  padding: 8px 12px;
  white-space: nowrap;
  color: var(--bleckit-btn, currentColor);
}

.cta.btn-outline {
  border-color: var(--bleckit-btn, currentColor);
}
.cta.btn-ghost {
  background: transparent;
}
.cta.btn-filled {
  background: var(--bleckit-btn-bg);
  color: var(--bleckit-btn-text, currentColor);
  border-color: var(--bleckit-btn-bg, transparent);
}

/* Credits */
.credits {
  margin: 0 auto;
  width: 100%;
  display: flex;
  align-items: baseline;
  gap: 8px;
  color: var(--bleckit-text-2, currentColor);
  font-size: 0.88em;
}
.credits-label {
  font-weight: 600;
  letter-spacing: 0.3px;
}
.credits-list {
  display: inline;
}
.credit {
  color: inherit;
  text-decoration: none;
}
.sep {
  opacity: 0.5;
  margin: 0 6px;
}

/* Responsive */
@media (max-width: 680px) {
  .inner {
    grid-template-columns: 1fr;
    text-align: center;
  }
  .cta {
    justify-self: center;
  }
  .credits {
    justify-content: center;
    flex-wrap: wrap;
    text-align: center;
  }
}

/* Reduced motion */
@media (prefers-reduced-motion: reduce) {
  .cta {
    transition: none;
  }
}
</style>
