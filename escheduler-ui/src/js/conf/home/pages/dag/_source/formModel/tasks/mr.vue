<template>
  <div class="spark-model">
    <m-list-box>
      <div slot="text">{{$t('Program Type')}}</div>
      <div slot="content">
        <x-select v-model="programType" :disabled="isDetails" style="width: 110px;">
          <x-option
                  v-for="city in programTypeList"
                  :key="city.code"
                  :value="city.code"
                  :label="city.code">
          </x-option>
        </x-select>
      </div>
    </m-list-box>
    <m-list-box v-if="programType !== 'PYTHON'">
      <div slot="text">{{$t('Main class')}}</div>
      <div slot="content">
        <x-input
                :disabled="isDetails"
                type="input"
                v-model="mainClass"
                :placeholder="$t('Please enter main class')"
                autocomplete="off">
        </x-input>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('Main jar package')}}</div>
      <div slot="content">
        <x-select
                style="width: 100%;"
                :placeholder="$t('Please enter main jar package')"
                v-model="mainJar"
                filterable
                :disabled="isDetails">
          <x-option
                  v-for="city in mainJarList"
                  :key="city.code"
                  :value="city.code"
                  :label="city.code">
          </x-option>
        </x-select>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('Command-line parameters')}}</div>
      <div slot="content">
        <x-input
                :autosize="{minRows:2}"
                :disabled="isDetails"
                type="textarea"
                v-model="mainArgs"
                :placeholder="$t('Please enter Command-line parameters')"
                autocomplete="off">
        </x-input>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('Other parameters')}}</div>
      <div slot="content">
        <x-input
                :disabled="isDetails"
                :autosize="{minRows:2}"
                type="textarea"
                v-model="others"
                :placeholder="$t('Please enter other parameters')"
                autocomplete="off">
        </x-input>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('Resources')}}</div>
      <div slot="content">
        <m-resources
                ref="refResources"
                @on-resourcesData="_onResourcesData"
                :resource-list="resourceList">
        </m-resources>
      </div>
    </m-list-box>
    <m-list-box>
      <div slot="text">{{$t('Custom Parameters')}}</div>
      <div slot="content">
        <m-local-params
                ref="refLocalParams"
                @on-local-params="_onLocalParams"
                :udp-list="localParams"
                :hide="false">
        </m-local-params>
      </div>
    </m-list-box>
  </div>
</template>
<script>
  import _ from 'lodash'
  import i18n from '@/module/i18n'
  import mListBox from './_source/listBox'
  import mResources from './_source/resources'
  import mLocalParams from './_source/localParams'
  import disabledState from '@/module/mixin/disabledState'
  export default {
    name: 'mr',
    data () {
      return {
        // Main function class
        mainClass: '',
        // Master jar package
        mainJar: null,
        // Main jar package (List)
        mainJarList: [],
        // Resource(list)
        resourceList: [],
        // Custom parameter
        localParams: [],
        // Command line argument
        mainArgs: '',
        // Other parameters
        others: '',
        // Program type
        programType: 'JAVA',
        // Program type(List)
        programTypeList: [{ code: 'JAVA' }, { code: 'PYTHON' }]
      }
    },
    props: {
      backfillItem: Object
    },
    mixins: [disabledState],
    methods: {
      /**
       * return localParams
       */
      _onLocalParams (a) {
        this.localParams = a
      },
      /**
       * return resourceList
       */
      _onResourcesData (a) {
        this.resourceList = a
      },
      /**
       * verification
       */
      _verification () {
        if (this.programType !== 'PYTHON' && !this.mainClass) {
          this.$message.warning(`${i18n.$t('Please enter main class')}`)
          return false
        }

        if (!this.mainJar) {
          this.$message.warning(`${i18n.$t('Please enter main jar package')}`)
          return false
        }

        if (!this.$refs.refResources._verifResources()) {
          return false
        }

        // localParams Subcomponent verification
        if (!this.$refs.refLocalParams._verifProp()) {
          return false
        }

        // storage
        this.$emit('on-params', {
          mainClass: this.mainClass,
          mainJar: {
            res: this.mainJar
          },
          resourceList: this.resourceList,
          localParams: this.localParams,
          mainArgs: this.mainArgs,
          others: this.others,
          programType: this.programType
        })
        return true
      },
      /**
       * Get resource data
       */
      _getResourcesList () {
        return new Promise((resolve, reject) => {
          let isJar = (alias) => {
            return alias.substring(alias.lastIndexOf('.') + 1, alias.length) !== 'jar'
          }
          this.mainJarList = _.map(_.cloneDeep(this.store.state.dag.resourcesListS), v => {
            return {
              id: v.id,
              code: v.alias,
              disabled: isJar(v.alias)
            }
          })
          resolve()
        })
      }
    },
    watch: {
      /**
       * monitor
       */
      programType (type) {
        if (type === 'PYTHON') {
          this.mainClass = ''
        }
      }
    },
    created () {
      this._getResourcesList().then(() => {
        let o = this.backfillItem

        // Non-null objects represent backfill
        if (!_.isEmpty(o)) {
          this.mainClass = o.params.mainClass || ''
          this.mainJar = o.params.mainJar.res || ''
          this.mainArgs = o.params.mainArgs || ''
          this.others = o.params.others
          this.programType = o.params.programType || 'JAVA'

          // backfill resourceList
          let resourceList = o.params.resourceList || []
          if (resourceList.length) {
            this.resourceList = resourceList
          }

          // backfill localParams
          let localParams = o.params.localParams || []
          if (localParams.length) {
            this.localParams = localParams
          }
        }
      })
    },
    mounted () {

    },
    components: { mLocalParams, mListBox, mResources }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .spark-model {
    .list-box-4p {
      .list {
        margin-bottom: 14px;
        .sp1 {
          float: left;
          width: 112px;
          text-align: right;
          margin-right: 10px;
          font-size: 14px;
          color: #777;
          display: inline-block;
          padding-top: 6px;
        }
        .sp2 {
          float: left;
          margin-right: 4px;
        }
      }
    }
  }
</style>
